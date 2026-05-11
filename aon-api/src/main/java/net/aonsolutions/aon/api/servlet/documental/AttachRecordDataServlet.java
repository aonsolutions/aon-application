package net.aonsolutions.aon.api.servlet.documental;

import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.AttachJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.attachment.Attach;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AttachRecordDataServlet", urlPatterns = { "/ms/api/attach/recordData" })
public class AttachRecordDataServlet extends AonApiHttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AttachRecordDataServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[POST] /ms/api/attach/recordData - Attach + RecordData update");

        AonApiData api = initialize(req);
        JSONObject json = api.getData();

        // 1. Convertir JSON a Attach
        Attach attach = AttachJSON.fromJSON(json);
        Integer attachId = attach.getId() != null ? attach.getId() : null;

        // 2. Insertar o actualizar attach
        if (attach.getId() != null) {
            AON.updateAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), attach);
        } else {
            attachId = AON.insertAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), attach);
        }

        // 3. Obtener recordDataId del JSON
        Integer recordDataId = JsonUtils.getInteger(json, "recordDataId");

        if (recordDataId != null) {
            // 4. Actualizar el recordData con el nuevo attachId
            AON.updateRecordDataAttach(
                api.getDomain().getName(),
                api.getDomain().getId(),
                api.getUser().getLogin(),
                recordDataId,
                attachId
            );
        }

        // 5. Respuesta simple
        JSONObject ok = new JSONObject();
        ok.put("ok", true);
        response(req, resp, ok);
    }
}
