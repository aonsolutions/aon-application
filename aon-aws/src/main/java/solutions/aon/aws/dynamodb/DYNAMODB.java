package solutions.aon.aws.dynamodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;

import solutions.aon.aws.AWS;

public class DYNAMODB {

	private DYNAMODB() {
	
	}
	
	private static AmazonDynamoDB connect() {
		return AmazonDynamoDBClientBuilder.standard()
				.withCredentials(AWS.getProvider())
				.withRegion("eu-west-1")
				.build();
	}

	public static Map<String, AttributeValue> scan(String table, String key, String value) {
		AmazonDynamoDB client = connect();
		String name = "#"+key;
		String val = ":"+ key;
		Map<String,String> expressionAttributesNames = new HashMap<>();
			expressionAttributesNames.put(name, key);
		Map<String,AttributeValue> expressionAttributeValues = new HashMap<>();
			expressionAttributeValues.put(val, new AttributeValue().withS(value));
			
		ScanRequest req = new ScanRequest(table)
			.withFilterExpression(name + "=" + val)
			.withExpressionAttributeNames(expressionAttributesNames)
		    .withExpressionAttributeValues(expressionAttributeValues);
		List<Map<String, AttributeValue>> list = client.scan(req).getItems();
		return list.isEmpty() ? new HashMap<>() : list.get(0);
	}
	
	   public static List<Map<String, AttributeValue>> scan(String table, String key, List<String> values) {
	        AmazonDynamoDB client = connect();
	        String name = "#"+key;
	        String val = ":"+ key;
	        Map<String,String> expressionAttributesNames = new HashMap<>();
	            expressionAttributesNames.put(name, key);
	        
	        Map<String,AttributeValue> expressionAttributeValues = new HashMap<>();
	        values.stream().forEach(value -> expressionAttributeValues.put(val, new AttributeValue().withS(value)));
	            
	        ScanRequest req = new ScanRequest(table)
	            .withFilterExpression(name + "=" + val)
	            .withExpressionAttributeNames(expressionAttributesNames)
	            .withExpressionAttributeValues(expressionAttributeValues);
	        return client.scan(req).getItems();
	    }
	
	public static Map<String, AttributeValue> get(String table, String key, String value) {
		AmazonDynamoDB client = connect();
		HashMap<String,AttributeValue> map = new HashMap<>();
		map.put(key, new AttributeValue(value));
		return client.getItem(new GetItemRequest()
				.withKey(map)
				.withTableName(table))
			.getItem();
	}
	
	public static void put(String table, Map<String, AttributeValue> item) {
		AmazonDynamoDB client = connect();
		client.putItem(table, item);
	}
	
	public static void update(String table, String key, String keyValue, String param, String value) {
        AmazonDynamoDB client = connect();
        
        HashMap<String,AttributeValue> map = new HashMap<>();
        map.put(key, new AttributeValue(keyValue));

        AttributeValueUpdate att = new AttributeValueUpdate();
        att.setAction(AttributeAction.PUT);
        att.setValue(new AttributeValue(value));
        HashMap<String,AttributeValueUpdate> mapUpdate = new HashMap<>();
        mapUpdate.put(param, att);
        
        UpdateItemRequest req = new UpdateItemRequest(table, map, mapUpdate);
        client.updateItem(req);
	}

	public static boolean exist(String table) {
		AmazonDynamoDB client = connect();		
		ListTablesResult list = client.listTables();
		return list.getTableNames().contains(table);
	}
	
	public static void createTable(String table, String key) {
		AmazonDynamoDB client = connect();
		
		List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        List<KeySchemaElement> keySchema = new ArrayList<>();
		attributeDefinitions.add(new AttributeDefinition().withAttributeName(key).withAttributeType("S"));
        keySchema.add(new KeySchemaElement().withAttributeName(key).withKeyType(KeyType.HASH));

		CreateTableRequest request = new CreateTableRequest().withTableName(table).withKeySchema(keySchema)
            .withAttributeDefinitions(attributeDefinitions).withProvisionedThroughput(
                new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(6L));
		
		client.createTable(request);
	}
	
}
