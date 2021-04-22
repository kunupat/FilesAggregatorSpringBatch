package com.test.spring.batch.routes;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilesAggregatorRoute extends RouteBuilder {

	@Autowired
	FileAggregationStrategy fileAggregationStrategy;

	@Override
	public void configure() throws Exception {

		String inputFilePath = System.getProperty("user.home") + "/data/out/jsons/";
		String outputFilePath = System.getProperty("user.home") + "/data/out/processed/";
		
		File file = new File(inputFilePath);
		int numberOfFiles= file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".json");
			}
			
		}).length;
		
		System.out.println(numberOfFiles);
		from("direct:start")
		.noAutoStartup()
		.loop(numberOfFiles)
			.pollEnrich("file:" + inputFilePath + "?noop=true",fileAggregationStrategy)
			.to("file:" + outputFilePath + "?fileName=customers.json")
		.routeId("filesAggregationRoute");
		
	}
}
