package de.htwg.bigdata.visual.visualisationclient;

import java.io.InputStream;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class InputStreamParser {
	private int fieldSize;
	
	public InputStreamParser(int fieldSize) {
		this.fieldSize = fieldSize;
	}
	
	public List<SimulationStep> getData(InputStream is) {
		
		List<SimulationStep> data = new LinkedList<SimulationStep>();
		
		JsonReader reader = Json.createReader(is);
		//JsonObject obj = reader.readObject();
		JsonArray steps = reader.readArray();
		//JsonArray steps = obj.getJsonArray("");
//		JsonArray steps = obj.getJsonArray("steps");
		System.out.println("InputStreamParser: Beginne Steps zu laden");
		for (JsonObject step : steps.getValuesAs(JsonObject.class)) {
			System.out.println("�u�ere Vorschleife");
			int id = step.getInt("step");
			int time = step.getInt("time");
			SimulationStep simulationStep = new SimulationStep(id, time, fieldSize);
			JsonArray fields = step.getJsonArray("fields");
			for (JsonObject field : fields.getValuesAs(JsonObject.class)) {
				System.out.println("innere Vorschleife");
				System.out.println(field);
				simulationStep.setField(
						field.getInt("newX"), 
						field.getInt("newY"), 
						field.getInt("concentration")
				);
			}
			data.add(simulationStep);
			System.out.println("Step " + id + " geladen");
		}
		
		System.out.println("Datenset wird sortiert");
		data.sort(new Comparator<SimulationStep>() {
			public int compare(SimulationStep arg0, SimulationStep arg1) {
				return arg0.compareTo(arg1);				
			}			
		});
		
		System.out.println("Datenset wird zur�ckgegeben");
		return data;
		
	}

}
