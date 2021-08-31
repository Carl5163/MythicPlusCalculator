package com.cwahler.mythicpluscalculator;



import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.util.StringUtils;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.ArrayList;  
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;



@Route
public class MainView extends VerticalLayout {

	private final DungeonRepository repo;

	final Grid<Dungeon> grid;

	TextField region = new TextField("Region");
	TextField realm = new TextField("Realm");
	TextField name = new TextField("Character Name");
	
	private Button reloadButton;

	// private final Button addNewBtn;

	public MainView(DungeonRepository repo) {
		this.repo = repo;
		this.grid = new Grid<>(Dungeon.class);

		HorizontalLayout actions = new HorizontalLayout(region, realm, name);
		add(actions);

		this.reloadButton = new Button("Load Actual Scores", VaadinIcon.RECYCLE.create());
		reloadButton.addClickListener(e -> {
			// save a couple of dungeons
			repo.deleteAll();
			repo.saveAll(getBestDungeons(region.getValue().trim(), realm.getValue().trim(), name.getValue().trim()));
			getAltDungeons(region.getValue().trim(), realm.getValue().trim(), name.getValue().trim(), repo);
			listDungeons("");
		});
		add(reloadButton);

		// build layout
		add(grid);

		grid.setColumns("name", "fortLevel", "tyranLevel", "fortScore", "tyranScore");
		grid.addColumn(new NumberRenderer<>(Dungeon::getTotalScore, "%(,.1f", getLocale())).setHeader("Total Score");

		// Initialize listing
		listDungeons(null);
	}

	// tag::listDungeons[]
	void listDungeons(String filterText) {
		if (!StringUtils.hasLength(filterText)) {
			grid.setItems(repo.findAll());
		}
		else {
			grid.setItems(repo.findByNameStartsWithIgnoreCase(filterText));
		}
	}
	// end::listDungeons[]

	private List<Dungeon> getBestDungeons(String region, String realm, String name) {
		List<Dungeon> dungeons = new ArrayList<Dungeon>();
		final String uri = "https://raider.io/api/v1/characters/profile?region=" + region + "&realm=" + realm + "&name=" + name + "&fields=mythic_plus_best_runs";
		RestTemplate restTemplate = new RestTemplate();

		try {
			JSONObject jo = (JSONObject)(new JSONParser().parse(restTemplate.getForObject(uri, String.class)));
			JSONArray dungArray = ((JSONArray)jo.get("mythic_plus_best_runs"));
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> itr = dungArray.iterator();
			while(itr.hasNext()) {
				JSONObject djson = (JSONObject) itr.next();

				JSONArray affixArray = ((JSONArray)djson.get("affixes"));
				Dungeon d = new Dungeon();
				if(((String)(((JSONObject)affixArray.get(0)).get("name"))).equals("Fortified")) {
					d = new Dungeon(
					(String)djson.get("dungeon"), 
					((Long)djson.get("mythic_level")).intValue(), 
					0, 
					((Double)djson.get("score")),
					0);
				} else {
					d = new Dungeon(
					(String)djson.get("dungeon"), 
					0, 
					((Long)djson.get("mythic_level")).intValue(), 
					0,
					((Double)djson.get("score")));
				}

				
				dungeons.add(d);
			}

		} catch (RestClientException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}


		return dungeons;
	}

	private void getAltDungeons(String region, String realm, String name, DungeonRepository repository) {
		final String uri = "https://raider.io/api/v1/characters/profile?region=" + region + "&realm=" + realm + "&name=" + name + "&fields=mythic_plus_alternate_runs";
		RestTemplate restTemplate = new RestTemplate();
		try {
			JSONObject jo = (JSONObject)(new JSONParser().parse(restTemplate.getForObject(uri, String.class)));
			JSONArray dungArray = ((JSONArray)jo.get("mythic_plus_alternate_runs"));
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> itr = dungArray.iterator();
			while(itr.hasNext()) {
				JSONObject djson = (JSONObject) itr.next();
				String dName = (String)djson.get("dungeon");
				List<Dungeon> queryResult = repository.findByNameStartsWithIgnoreCase(dName);
				if(queryResult.size() > 0) {
					Dungeon dBest = queryResult.get(0);

					JSONArray affixArray = ((JSONArray)djson.get("affixes"));
					if(((String)(((JSONObject)affixArray.get(0)).get("name"))).equals("Fortified")) {
						dBest.setFortLevel(((Long)djson.get("mythic_level")).intValue());
						dBest.setFortScore(((Number)djson.get("score")).doubleValue());
					} else {
						dBest.setTyranLevel(((Long)djson.get("mythic_level")).intValue());
						dBest.setTyranScore(((Number)djson.get("score")).doubleValue());
					}
					repository.save(dBest);
				}
			}
		} catch (RestClientException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
