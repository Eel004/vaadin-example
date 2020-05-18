package com.example.application.views.masterdetail;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.application.backend.BackendService;
import com.example.application.backend.domain.Employee;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.example.application.views.main.MainView;

import java.util.Arrays;
import java.util.List;

@Route(value = "master-detail", layout = MainView.class)
@PageTitle("Master-Detail")
@CssImport("./styles/views/masterdetail/master-detail-view.css")
public class MasterDetailView extends Div implements AfterNavigationObserver {

    private static final Logger LOG = LoggerFactory.getLogger(MasterDetailView.class);

    private static final List<String> SKILL = Arrays.asList("Java", ".Net", "PHP", "Go", "Python", "Angular", "React");

    private static final List<String> DEPARTMENT = Arrays.asList("A", "B", "C", "D", "E");

    private Grid<Employee> employeeGrid;

    private TextField firstname = new TextField();
    private TextField lastname = new TextField();
    private TextField email = new TextField();
    private ComboBox<String> department = new ComboBox<>();
    private ComboBox<String> skillCmb = new ComboBox<>();
    private TextArea notesTextArea = new TextArea();

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<Employee> binder;

    private Employee employee = new Employee();

    @Autowired
    private BackendService backendService;

    public MasterDetailView() {
        setId("master-detail-view");
        // Configure Grid
        employeeGrid = new Grid<>();
        employeeGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        employeeGrid.setHeightFull();
        employeeGrid.addColumn(Employee::getFirstName).setHeader("First name");
        employeeGrid.addColumn(Employee::getLastName).setHeader("Last name");
        employeeGrid.addColumn(Employee::getMainSkill).setHeader("Main skill");
        employeeGrid.addColumn(Employee::getEmail).setHeader("Email");
        employeeGrid.addColumn(Employee::getDepartment).setHeader("Title");
        employeeGrid.addColumn(Employee::getNotes).setHeader("Notes");

        //when a row is selected or deselected, populate form
        employeeGrid.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        // Configure Form
        binder = new Binder<>(Employee.class);

        // Bind fields. This where you'd define e.g. validation rules
        //binder.bindInstanceFields(this);
        // note that password field isn't bound since that property doesn't exist in
        // Employee
        binder.forField(firstname)
                .asRequired("Employee must have a first name")
                .bind(Employee::getFirstName, Employee::setFirstName);

        binder.forField(lastname)
                .asRequired("Employee must have a last name")
                .bind(Employee::getLastName, Employee::setLastName);

        binder.forField(email)
                .withValidator(new EmailValidator("Invalid email"))
                .bind(Employee::getEmail, Employee::setEmail);

        binder.forField(department)
                .bind(Employee::getDepartment, Employee::setDepartment);

        binder.forField(skillCmb)
                .asRequired("Employee must have skill")
                .bind(Employee::getMainSkill, Employee::setMainSkill);

        binder.forField(notesTextArea)
                .bind(Employee::getNotes, Employee::setNotes);

        // the grid valueChangeEvent will clear the form too
        cancel.addClickListener(e -> employeeGrid.asSingleSelect().clear());
        binder.readBean(employee);

        save.addClickListener(e -> {
            try {
                binder.writeBean(employee);
                backendService.saveEmployee(employee);
                reload();
            } catch (ValidationException ex) {
                LOG.error("Error while saving employee", ex);
            }
        });

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorDiv = new Div();
        editorDiv.setId("editor-layout");
        FormLayout formLayout = new FormLayout();
        addFormItem(editorDiv, formLayout, firstname, "First name");
        addFormItem(editorDiv, formLayout, lastname, "Last name");
        addFormItem(editorDiv, formLayout, email, "Email");
        department.setItems(DEPARTMENT);
        addFormItem(editorDiv, formLayout, department, "Department");
        skillCmb.setItems(SKILL);
        addFormItem(editorDiv, formLayout, skillCmb, "Skill");
        addFormItem(editorDiv, formLayout, notesTextArea, "Notes");
        createButtonLayout(editorDiv);
        splitLayout.addToSecondary(editorDiv);
    }

    private void createButtonLayout(Div editorDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(cancel, save);
        editorDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(employeeGrid);
    }

    private void addFormItem(Div wrapper, FormLayout formLayout,
            AbstractField field, String fieldName) {
        formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("red");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        employeeGrid.setItems(backendService.getEmployees());
    }

    private void populateForm(Employee value) {
        // Value can be null as well, that clears the form
        binder.readBean(value);

        // The password field isn't bound through the binder, so handle that
        //password.setValue("");
    }

    private void reload() {
        employeeGrid.setItems(backendService.getEmployees());
        employeeGrid.getDataProvider().refreshAll();
    }
}
