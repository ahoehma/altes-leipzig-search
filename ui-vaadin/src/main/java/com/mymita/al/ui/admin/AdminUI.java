package com.mymita.al.ui.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.mymita.al.domain.Person;
import com.mymita.al.domain.Person.Gender;
import com.mymita.al.domain.Tag;
import com.mymita.al.importer.ImportService;
import com.mymita.al.importer.ImportService.CountingImportListener;
import com.mymita.al.repository.PersonRepository;
import com.mymita.al.repository.TagRepository;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Configurable
@PreserveOnRefresh
@Theme(Reindeer.THEME_NAME)
public class AdminUI extends UI {

	class CsvUploader implements Receiver, SucceededListener {

		private File file;
		private final ProgressBar progressBar;

		public CsvUploader(final ProgressBar progressBar) {
			this.progressBar = progressBar;
		}

		@Override
		public OutputStream receiveUpload(final String filename,
				final String mimeType) {
			System.out.println("Receive upload " + filename);
			FileOutputStream fos = null;
			try {
				file = new File(Files.createTempDir(), filename);
				file.deleteOnExit();
				fos = new FileOutputStream(file);
			} catch (final java.io.FileNotFoundException e) {
				new Notification("Could not open file<br/>", e.getMessage(),
						Notification.Type.ERROR_MESSAGE)
						.show(Page.getCurrent());
				return null;
			}
			return fos;
		}

		@Override
		public void uploadSucceeded(final SucceededEvent event) {
			UI.getCurrent().access(new WorkThread(file, progressBar));
		}
	}

	class WorkThread extends Thread {
		private final File file;
		private final ProgressBar progressBar;

		public WorkThread(final File file, final ProgressBar progressBar) {
			this.file = file;
			this.progressBar = progressBar;
		}

		@Override
		public void run() {
			try {
				importer.importPersons(file,
						new CountingImportListener<Object>() {

							@Override
							public void onImport(final Object object) {
								super.onImport(object);
								if (object instanceof Person) {
									if (count(object) > 0) {
										progressBar.setValue(new Float(
												max(object) * 100.0
														/ count(object)));
									}
								}
							}

							@Override
							public void startImport(
									final Class<? extends Object> clazz,
									final int size) {
								super.startImport(clazz, size);
								if (clazz.equals(Person.class)) {
									progressBar.setEnabled(true);
								}
							}
						});
			} catch (final IOException e) {
				new Notification("Import failed<br/>", e.getMessage(),
						Notification.Type.ERROR_MESSAGE)
						.show(Page.getCurrent());
			}
			progressBar.setEnabled(false);
			file.delete();
		}
	};

	@Autowired
	transient PersonRepository personRepository;
	@Autowired
	transient TagRepository tagRepository;
	@Autowired
	transient ImportService importer;

	private Component createContent() {
		final VerticalLayout result = new VerticalLayout();

		result.addComponent(new Table(null, new BeanItemContainer<Person>(
				Person.class, ImmutableList.copyOf(personRepository.findAll()))));
		result.addComponent(new Table(null, new BeanItemContainer<Tag>(
				Tag.class, ImmutableList.copyOf(tagRepository.findAll()))));

		final FormLayout addLayout = new FormLayout();
		final TextField code = new TextField("Code");
		addLayout.addComponent(code);
		final TextField firstName = new TextField("FirstName");
		addLayout.addComponent(firstName);
		final TextField lastName = new TextField("LastName");
		addLayout.addComponent(lastName);
		final ComboBox gender = new ComboBox("Gender", Lists.newArrayList(
				Person.Gender.MALE, Person.Gender.FEMALE));
		gender.setConverter(Gender.class);
		addLayout.addComponent(gender);
		final TextField description = new TextField("Description");
		addLayout.addComponent(description);
		final DateField dateOfBirth = new DateField("DateOfBirth");
		addLayout.addComponent(dateOfBirth);
		final DateField dateOfDeath = new DateField("DateOfDeath");
		addLayout.addComponent(dateOfDeath);
		addLayout.addComponent(new NativeButton("Add",
				new Button.ClickListener() {

					@Override
					public void buttonClick(final Button.ClickEvent event) {
						// final Person p = new Person();
						// p.setCode(code.getValue());
						// p.setFirstName(firstName.getValue());
						// p.setLastName(lastName.getValue());
						// p.setGender((Gender) gender.getValue());
						// p.setDescription(description.getValue());
						// p.setDateOfBirth(SimpleDateFormat.getInstance().format(
						// dateOfBirth.getValue()));
						// p.setDateOfDeath(SimpleDateFormat.getInstance().format(
						// dateOfDeath.getValue()));
						// service.save(p);
					}
				}));
		result.addComponent(addLayout);

		final ProgressBar progressBar = new ProgressIndicator();
		progressBar.setEnabled(false);
		final CsvUploader receiver = new CsvUploader(progressBar);
		final Upload upload = new Upload(null, receiver);
		upload.setButtonCaption("Start Import");
		upload.addSucceededListener(receiver);
		final HorizontalLayout c = new HorizontalLayout(upload, progressBar);
		c.setSpacing(true);
		c.setComponentAlignment(upload, Alignment.MIDDLE_LEFT);
		c.setComponentAlignment(progressBar, Alignment.MIDDLE_RIGHT);
		result.addComponent(c);

		result.addComponent(new NativeButton("Delete ALL",
				new Button.ClickListener() {

					@Override
					public void buttonClick(final Button.ClickEvent event) {
						personRepository.deleteAll();
						tagRepository.deleteAll();
					}
				}));

		return result;
	}

	@Override
	protected void init(final VaadinRequest request) {
		getPage().setTitle("Altes Leipzig Suche - Administration");
		setSizeFull();
		setContent(createContent());
	}
}