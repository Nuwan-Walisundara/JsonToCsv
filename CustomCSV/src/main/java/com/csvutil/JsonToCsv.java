package com.csvutil;

import java.io.IOException;

import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;

public class JsonToCsv extends AbstractMediator { 

	public boolean mediate(MessageContext context) { 
		try {
			// Getting the json payload to string
			String jsonPayloadToString = JsonUtil
			.jsonPayloadToString(((Axis2MessageContext) context)
			.getAxis2MessageContext());
			
			// Make a json object
			JSONObject jsonBody = new JSONObject(jsonPayloadToString);
			String transformedJson = jsonBody.toString();
			
			JsonNode jsonTree = new ObjectMapper().readTree( jsonPayloadToString.getBytes());
			
			Builder csvSchemaBuilder = CsvSchema.builder();
			JsonNode firstObject = jsonTree.elements().next();
			firstObject.fieldNames().forEachRemaining(fieldName -> {csvSchemaBuilder.addColumn(fieldName);} );
			CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
			
	
		

			
			CsvMapper csvMapper = new CsvMapper();
			String  outPutCsv= csvMapper.writerFor(JsonNode.class)
									  .with(csvSchema)
									  .writeValueAsString(jsonTree);
			
			
			// Setting the new json payload.
			JsonUtil.newJsonPayload(
			((Axis2MessageContext) context).getAxis2MessageContext(),
			outPutCsv, true, true);
			
				return true;
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
