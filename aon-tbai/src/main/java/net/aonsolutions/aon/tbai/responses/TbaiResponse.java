package net.aonsolutions.aon.tbai.responses;

import java.util.Date;
import java.util.Optional;

public class TbaiResponse {

	private Date 	reception_date;
	private Integer status;
	private String 	description;
	private String 	description_eus;
	private String 	validation_code;
	private String 	validation_description;
	private String 	validation_description_eus;

	public TbaiResponse() {}

	public Optional<Date> getReception_date() 						{return Optional.ofNullable(reception_date);}
	public TbaiResponse setReception_date(Date reception_date) 		{this.reception_date = reception_date; 		return this;}

	public Optional<Integer> getStatus() 							{return Optional.ofNullable(status);}
	public TbaiResponse setStatus(Integer status) 					{this.status = status;						return this;}

	public Optional<String> getDescription() 						{return Optional.ofNullable(description);}
	public TbaiResponse setDescription(String description) 			{this.description = description;			return this;}
	
	public Optional<String> getDescription_eus() 					{return Optional.ofNullable(description_eus);}
	public TbaiResponse setDescription_eus(String description_eus) 	{this.description_eus = description_eus;	return this;}

	public Optional<String> getValidation_code() 					{return Optional.ofNullable(validation_code);}
	public TbaiResponse setValidation_code(String validation_code) 	{this.validation_code = validation_code;	return this;}

	public Optional<String> getValidation_description() 									{return Optional.ofNullable(validation_description);}
	public TbaiResponse setValidation_description(String validation_description) 			{this.validation_description = validation_description;			return this;}
	
	public Optional<String> getValidation_description_eus() 								{return Optional.ofNullable(validation_description_eus);}
	public TbaiResponse setValidation_description_eus(String validation_description_eus) 	{this.validation_description_eus = validation_description_eus;	return this;}

}
