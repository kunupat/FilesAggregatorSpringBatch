package com.test.spring.batch.routes;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.codehaus.jettison.json.JSONArray;
import org.springframework.stereotype.Component;

@Component
public class FileAggregationStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		if (oldExchange == null) {
			newExchange.setProperty(Exchange.AGGREGATION_COMPLETE_ALL_GROUPS, true);
			return newExchange;
		}

		try {

			String body1 = oldExchange.getIn().getBody(String.class);
			String body2 = newExchange.getIn().getBody(String.class);
			
			JSONArray sourceArray = new JSONArray(body2);
			JSONArray destinationArray = new JSONArray(body1);

			for (int i = 0; i < sourceArray.length(); i++) {
				destinationArray.put(sourceArray.getJSONObject(i));
			}

			String result = destinationArray.toString();

			oldExchange.getIn().setBody(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oldExchange;
	}

}
