package com.cwahler.mpluscalc;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

/**
 * A simple example to introduce building forms. As your real application is probably much
 * more complicated than this example, you could re-use this form in multiple places. This
 * example component is only used in MainView.
 * <p>
 * In a real world application you'll most likely using a common super class for all your
 * forms - less code, better UX.
 */
@SpringComponent
@UIScope
public class DungeonEditor extends VerticalLayout implements KeyNotifier {

	private final DungeonRepository repository;

	/**
	 * The currently edited dungeon
	 */
	private Dungeon dungeon;

	/* Fields to edit properties in Dungeon entity */
	TextField fortLevel = new TextField("fortLevel");
	TextField tyranLevel = new TextField("tyranLevel");
	TextField percentRemaining = new TextField("percentRemaining");

	/* Action buttons */
	// TODO why more code?
	Button save = new Button("Save", VaadinIcon.CHECK.create());
	Button cancel = new Button("Cancel");
	HorizontalLayout actions = new HorizontalLayout(save, cancel);

	Binder<Dungeon> binder = new Binder<>(Dungeon.class);
	private ChangeHandler changeHandler;

	@Autowired
	public DungeonEditor(DungeonRepository repository) {
		this.repository = repository;
		this.setWidth(this.getMaxWidth());
		Span pRem = new Span("Percentage(%) Remaining (Negative for Overtime, Positive for Undertime)");
		percentRemaining.setLabel("");
		percentRemaining.setValue("0");
		
		fortLevel.setLabel("Fortified Level");
		tyranLevel.setLabel("Tyranical Level");
		
		add(fortLevel, tyranLevel, pRem, percentRemaining, actions);


		binder.forField ( this.fortLevel )
        .withNullRepresentation ( "" )
        .withConverter ( new StringToIntegerConverter ( Integer.valueOf ( 0 ), "integers only" ) )
        .bind ( Dungeon::getFortLevel, Dungeon::setFortLevel );
		binder.forField ( this.tyranLevel )
        .withNullRepresentation ( "" )
        .withConverter ( new StringToIntegerConverter ( Integer.valueOf ( 0 ), "integers only" ) )
        .bind ( Dungeon::getTyranLevel, Dungeon::setTyranLevel );
		binder.forField ( this.percentRemaining )
        .withNullRepresentation ( "" )
        .withConverter ( new StringToDoubleConverter ( Double.valueOf ( 0 ), "doubles only" ) )
        .bind ( Dungeon::getPercentRemaining, Dungeon::setPercentRemaining );
		// bind using naming convention
		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);

		save.getElement().getThemeList().add("primary");

		addKeyPressListener(Key.ENTER, e -> save());

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> save());
		cancel.addClickListener(e -> editDungeon(dungeon));
		setVisible(false);
	}

	void save() {
		repository.save(dungeon);
		changeHandler.onChange();
	}

	public interface ChangeHandler {
		void onChange();
	}

	public final void editDungeon(Dungeon c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			dungeon = repository.findById(c.getId()).get();
		}
		else {
			dungeon = c;
		}
		cancel.setVisible(persisted);

		// Bind dungeon properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		
		binder.setBean(dungeon);

		setVisible(true);

		// Focus first name initially
		fortLevel.focus();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		changeHandler = h;
	}

}