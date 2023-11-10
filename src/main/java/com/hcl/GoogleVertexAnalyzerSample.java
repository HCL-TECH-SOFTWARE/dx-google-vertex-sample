/*
 * Copyright 2023 HCL America, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hcl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hcl.workplace.wcm.restv2.ai.*;
import com.ibm.workplace.wcm.rest.exception.AIGenerationException;
import com.google.cloud.aiplatform.v1beta1.EndpointName;
import com.google.cloud.aiplatform.v1beta1.PredictResponse;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1beta1.PredictionServiceSettings;
import com.google.protobuf.Value;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.util.JsonFormat;
import java.io.IOException;

public class GoogleVertexAnalyzerSample implements IAIGeneration {

	private static final String PROJECT = "your-project-id-google";
	private static final String LOCATION = "us-central1";
	private static final String PUBLISHER = "google";
	private static final String MODEL = "text-bison@001";
	private static final String CLOSING_DELIMITER = " ###";

	@Override
	public String generateSummary(List<String> values) throws AIGenerationException {

		String prediction = "";

		String instance = "{ \"content\": \"" + "Provide a 2 sentence summary of the following text: ###"+ values.toString().replaceAll("\"","\\\\\"") + CLOSING_DELIMITER + "\"}";
		String parameters = "{\n" + "  \"temperature\": 0.2,\n" + "  \"topP\": 0,\n" + "  \"maxOutputTokens\": 256,\n"
				+ "  \"topK\": 1\n" + "}";
		try {
			prediction = predictText(instance, parameters, PROJECT, LOCATION, PUBLISHER, MODEL);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AIGenerationException(e);
		}

		return prediction;
	}

	@Override
	public List<String> generateKeywords(List<String> values) throws AIGenerationException {
		List<String> predictionList = new ArrayList<String>();
		String instance = "{ \"content\": \"" + "Provide 5 keywords for the following text as unnumbered list separated by semicolon: ###"+ values.toString().replaceAll("\"","\\\\\"") + CLOSING_DELIMITER + "\"}";
		String parameters = "{\n" + "  \"temperature\": 0.2,\n" + "  \"maxOutputTokens\": 256,\n" + "  \"topP\": 0,\n"
				+ "  \"topK\": 1\n" + "}";
		try {
			String result = predictText(instance, parameters, PROJECT, LOCATION, PUBLISHER, MODEL);
			
			//parse result
			if (result.startsWith("\n")) {
				result = result.substring(1);
			}
			if (result.startsWith("* ")) {
				result = result.substring(2);
			}

			predictionList = Arrays.asList(result.split("\\n\\* "));

		} catch (IOException e) {
			e.printStackTrace();
			throw new AIGenerationException(e);
		}

		return predictionList;
	}

	@Override
	public Sentiment generateSentiment(List<String> values) throws AIGenerationException {
		Sentiment sentiment = null;
		String instance = "{ \"content\": \"" + "Rate the following into Highly negative, negative, neutral, positive or Highly positive: ###"+ values.toString().replaceAll("\"","\\\\\"") + CLOSING_DELIMITER + "\"}";
		String parameters = "{\n" + "  \"temperature\": 0.2,\n" + "  \"maxDecodeSteps\": 5,\n" + "  \"topP\": 0,\n"
				+ "  \"topK\": 1\n" + "}";
		try {
			String sentimentString = predictText(instance, parameters, PROJECT, LOCATION, PUBLISHER, MODEL);
			sentiment = Sentiment.fromDescription(sentimentString.trim());
		} catch (IOException e) {
			e.printStackTrace();
			throw new AIGenerationException(e);
		}
		return sentiment;
	}

	static String predictText(String instance, String parameters, String project, String location,
			String publisher, String model) throws IOException {
		String result="positive";
		String endpoint = String.format("%s-aiplatform.googleapis.com:443", location);
		PredictionServiceSettings predictionServiceSettings = PredictionServiceSettings.newBuilder()
				.setEndpoint(endpoint).build();

		// Initialize client that will be used to send requests. This client only needs
		// to be created
		// once, and can be reused for multiple requests.
		try (PredictionServiceClient predictionServiceClient = PredictionServiceClient
				.create(predictionServiceSettings)) {
			final EndpointName endpointName = EndpointName.ofProjectLocationPublisherModelName(project, location,
					publisher, model);

			// Use Value.Builder to convert instance to a dynamically typed value that can
			// be
			// processed by the service.
			Value.Builder instanceValue = Value.newBuilder();
			JsonFormat.parser().merge(instance, instanceValue);
			List<Value> instances = new ArrayList();
			instances.add(instanceValue.build());

			// Use Value.Builder to convert parameter to a dynamically typed value that can
			// be
			// processed by the service.
			Value.Builder parameterValueBuilder = Value.newBuilder();
			JsonFormat.parser().merge(parameters, parameterValueBuilder);
			Value parameterValue = parameterValueBuilder.build();

			PredictResponse predictResponse = predictionServiceClient.predict(endpointName, instances, parameterValue);
			List<Value> predictions = predictResponse.getPredictionsList();
			for (Iterator iterator = predictions.iterator(); iterator.hasNext();) {
				Value value = (Value) iterator.next();
				result = value.getStructValue().getFieldsOrThrow("content").getStringValue();
				if(result!=null)
					break;
			}
			return result;
		}
	}
}