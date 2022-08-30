package aon.sepe.objects;

import java.util.Date;
import java.util.Optional;

public class CopyBasic {

	private String workAddress;
	private String restContract;
	private FirmType firmType;

	// SEARCH FOR SEPEID
	private String sepeId;
	// --------OR---------
	// SEARCH FOR IPF
	private String ipf;
	private Date fini;
	private Date fend;

	public String getWorkAddress() {
		return workAddress;
	}

	public String getRestContract() {
		return restContract;
	}

	public FirmType getFirmType() {
		return firmType;
	}

	public Optional<String> getIpf() {
		return Optional.ofNullable(ipf);
	}

	public Optional<Date> getFini() {
		return Optional.ofNullable(fini);
	}

	public Optional<Date> getFend() {
		return Optional.ofNullable(fend);
	}

	public Optional<String> getSepeId() {
		return Optional.ofNullable(sepeId);
	}

	public CopyBasic setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
		return this;
	}

	public CopyBasic setRestContract(String restContract) {
		this.restContract = restContract;
		return this;
	}

	public CopyBasic setFirmType(FirmType firmType) {
		this.firmType = firmType;
		return this;
	}

	public CopyBasic setIpf(String ipf) {
		this.ipf = ipf;
		return this;
	}

	public CopyBasic setFini(Date fini) {
		this.fini = fini;
		return this;
	}

	public CopyBasic setFend(Date fend) {
		this.fend = fend;
		return this;
	}

	public CopyBasic setSepeId(String sepeId) {
		this.sepeId = sepeId;
		return this;
	}

	public enum FirmType {
		FIRMADA_REPRESENTANTES_LEGALES(1), NO_EXISTE_REPRESENTACION(2), NO_FACILITADO_COPIA(3), REHUSAN_FIRMAR(4);

		private Integer value;

		private FirmType(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	@Override
	public String toString() {
		return "CopyBasic [workAddress=" + workAddress + ", restContract=" + restContract + ", firmType=" + firmType
				+ ", sepeId=" + sepeId + ", ipf=" + ipf + ", fini=" + fini + ", fend=" + fend + "]";
	}

}
