package solutions.aon.aws.dynamodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

public class DYNAMODB {

	private DYNAMODB() {
	
	}
	
	private static DynamoDbClient connect() {
		return DynamoDbClient.builder()
                .region(Region.EU_WEST_1)
                .build();
	}

	public static Map<String, AttributeValue> get2(String table, String key, String value) {
		DynamoDbClient client = connect();
		try {
			String name = "#"+key;
			String val = ":"+ key;
			Map<String,String> expressionAttributesNames = new HashMap<>();
			expressionAttributesNames.put(name, key);
			Map<String,AttributeValue> expressionAttributeValues = new HashMap<>();
			expressionAttributeValues.put(val, AttributeValue.builder().s(value).build());
			
			ScanRequest request = ScanRequest.builder()
					.tableName(table)
					.filterExpression(name + "=" + val)
					.expressionAttributeNames(expressionAttributesNames)
					.expressionAttributeValues(expressionAttributeValues)
					.build();

			List<Map<String, AttributeValue>> list = client.scan(request).items();
			return list.isEmpty() ? new HashMap<>() : list.get(0);
		} finally {
			client.close();
		}
	}
	
	public static Map<String, AttributeValue> get(String table, String key, String value) {
		DynamoDbClient client = connect();
		try {
			HashMap<String,AttributeValue> map = new HashMap<>();
			map.put(key, AttributeValue.builder().s(value).build());
			
			GetItemRequest request = GetItemRequest.builder().key(map).tableName(table).build();
			
			return client.getItem(request).item();			
		} finally {
			client.close();
		}
	}
	
	public static void put(String table, Map<String, AttributeValue> item) {
		DynamoDbClient client = connect();
		try {
			PutItemRequest request = PutItemRequest.builder()
	                .tableName(table)
	                .item(item)
	                .build();
			
			client.putItem(request);
		} finally {
			client.close();
		}
	}

	public static boolean exist(String table) {
		DynamoDbClient client = connect();		
		try {
			ListTablesRequest request = ListTablesRequest.builder().build();
			ListTablesResponse response = client.listTables(request);
			return response.tableNames().contains(table);
		} finally {
			client.close();
		}
	}
	
	public static void createTable(String table, String key) {
		DynamoDbClient client = connect();
		try {
			CreateTableRequest createTableRequest = CreateTableRequest.builder()
				.attributeDefinitions(AttributeDefinition.builder().attributeName(key).attributeType(ScalarAttributeType.S).build())
				.keySchema(KeySchemaElement.builder().attributeName(key).keyType(KeyType.HASH).build())
				.provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(6L).build())
				.tableName(table)
				.build();
			
			client.createTable(createTableRequest);
		} finally {
			client.close();
		}
	}
	
}
