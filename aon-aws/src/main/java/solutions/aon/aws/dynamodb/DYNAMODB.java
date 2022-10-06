package solutions.aon.aws.dynamodb;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;

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
	
	public static Map<String, AttributeValue> get(String table, String key, String value) {
		AmazonDynamoDB client = connect();
		HashMap<String,AttributeValue> map = new HashMap<>();
		map.put(key, new AttributeValue(value));
		return client.getItem(new GetItemRequest()
				.withKey(map)
				.withTableName(table))
			.getItem();
	}
	
	
}
