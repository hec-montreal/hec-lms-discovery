package ca.hec.web.service.consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse
{
	private int statusCode;
	private String query;
	private String result;
}
