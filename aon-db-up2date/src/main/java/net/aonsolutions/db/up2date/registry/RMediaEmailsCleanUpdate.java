package net.aonsolutions.db.up2date.registry;

import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RMediaEmailsCleanUpdate implements Update {

    public static final RMediaEmailsCleanUpdate RMEDIA_EMAILS_CLEAN_UPDATE = new RMediaEmailsCleanUpdate();

    private RMediaEmailsCleanUpdate() {}

    @Override
    public void upgrade(Connection connection) {
        Settings settings = new Settings();
        settings.setRenderSchema(false);
        settings.setParamType(ParamType.INLINED);

        DSLContext dsl = DSL.using(connection, SQLDialect.MARIADB, settings);

        // Leer todos los registros media = 4
        List<Record2<Integer, String>> rows =
            dsl.select(RMEDIA.ID, RMEDIA.VALUE)
               .from(RMEDIA)
               .where(RMEDIA.MEDIA.eq((byte)4))
               .fetch();

        for (Record2<Integer, String> row : rows) {
            Integer id = row.value1();
            String value = row.value2();

            String cleaned = cleanEmailListSafely(value);

            // Solo actualizar si realmente cambia
            if (!Objects.equals(value, cleaned)) {

                System.out.println(
                    "[RMEDIA EMAIL CLEAN] ID=" + id +
                    " | ORIGINAL='" + value + "'" +
                    " | CLEANED='" + cleaned + "'" +
                    " | CHANGE=" + describeChange(value, cleaned)
                );

                dsl.update(RMEDIA)
                   .set(RMEDIA.VALUE, cleaned)
                   .where(RMEDIA.ID.eq(id))
                   .execute();
            }
        }
    }

    // ---------------------------------------------------------
    // LIMPIEZA SEGURA DE EMAILS
    // ---------------------------------------------------------

    public static String cleanEmailListSafely(String value) {

        if (value == null || value.isEmpty()) {
            return value; // vacío es válido
        }

        // 1. Eliminar caracteres de control globalmente
        value = value.replaceAll("\\p{Cntrl}", "");

        // 2. Separar por comas
        String[] parts = value.split(",");

        List<String> cleaned = new ArrayList<>();

        for (String part : parts) {
            String email = part.trim(); // limpiar espacios alrededor

            if (email.isEmpty()) {
                continue; // emails vacíos dentro de la lista se eliminan
            }

            // 3. Eliminar espacios internos (si hay, es seguro eliminarlos)
            email = email.replace(" ", "");

            // 4. Validar email
            if (isValidEmailStrict(email)) {
                cleaned.add(email);
            } else {
                // 5. Intento de corrección segura
                String corrected = trySafeCorrections(email);

                if (corrected != null && isValidEmailStrict(corrected)) {
                    cleaned.add(corrected);
                } else {
                    // 6. Email dudoso - se deja tal cual
                    cleaned.add(email);
                }
            }
        }

        // 7. Reconstruir lista
        return String.join(",", cleaned);
    }

    public static String trySafeCorrections(String email) {

        String original = email;

        // Eliminar caracteres de control
        email = email.replaceAll("\\p{Cntrl}", "");

        // Eliminar espacios internos
        email = email.replace(" ", "");

        // Eliminar comas accidentales dentro del email
        email = email.replace(",", "");

        // Si el email queda igual que el original y sigue siendo inválido - no se corrige
        if (email.equals(original)) {
            return null;
        }

        return email;
    }

    public static boolean isValidEmailStrict(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // ---------------------------------------------------------
    // DESCRIPCIÓN DEL CAMBIO PARA LOGGING
    // ---------------------------------------------------------

    public static String describeChange(String original, String cleaned) {

        if (original.equals(cleaned)) {
            return "NO CHANGE";
        }

        if (cleaned.isEmpty()) {
            return "REMOVED EMPTY EMAILS";
        }

        if (original.replaceAll("\\p{Cntrl}", "").equals(cleaned)) {
            return "REMOVED CONTROL CHARS";
        }

        if (original.trim().equals(cleaned)) {
            return "TRIMMED SPACES";
        }

        if (original.replace(" ", "").equals(cleaned)) {
            return "REMOVED INTERNAL SPACES";
        }

        if (original.replace(",", "").equals(cleaned)) {
            return "REMOVED EXTRA COMMAS";
        }

        return "PARTIAL SAFE CLEAN";
    }
    
    // Esta SQL muestra todos los mails erroneos
    /**
     	SELECT id,
		       value,
		       CASE
		           WHEN value = '' THEN 'Valid (empty)'
		           WHEN value REGEXP '[[:cntrl:]]' THEN 'Control chars'
		           WHEN value REGEXP '^[[:space:]]|[[:space:]]$' THEN 'Spaces at ends'
		           WHEN value REGEXP '[[:space:]]' THEN 'Spaces inside'
		           WHEN value REGEXP '[^A-Za-z0-9._%+,-@]' THEN 'Special chars'
		           WHEN NOT value REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}(,[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,})*$'
		                THEN 'Invalid email format'
		       END AS reason
		FROM rmedia
		WHERE media = 4
		  AND value <> ''   -- vacío es válido
		  AND (
		        value REGEXP '[[:cntrl:]]'
		        OR value REGEXP '^[[:space:]]|[[:space:]]$'
		        OR value REGEXP '[[:space:]]'
		        OR value REGEXP '[^A-Za-z0-9._%+,-@]'
		        OR NOT value REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}(,[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,})*$'
		      );
     **/
}
