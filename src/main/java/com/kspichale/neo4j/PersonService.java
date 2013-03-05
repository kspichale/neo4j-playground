package com.kspichale.neo4j;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

	@Autowired
	private PersonRepository personRepository;

	public long getNumberOfPersons() {
		return personRepository.count();
	}

	public Person save(Person person) {
		return personRepository.save(person);
	}

	public Set<Person> createPersons() {
		Person john = personRepository.save(new Person("John"));
		Person steve = personRepository.save(new Person("Steve"));
		Person jack = personRepository.save(new Person("Jack"));

		steve.knows(john);
		steve.knows(jack);

		// Persist created relationships to graph database
		steve = personRepository.save(steve);

		// Update relationships
		john = personRepository.findOne(john.getId());
		jack = personRepository.findOne(jack.getId());

		Set<Person> persons = new HashSet<Person>();
		persons.add(john);
		persons.add(steve);
		persons.add(jack);

		return persons;
	}

	public Iterable<Person> getAllPersons() {
		return personRepository.findAll();
	}

	public Person findByName(String name) {
		return personRepository.findByPropertyValue("name", name);
	}
	
	public Iterable<Person> getFriends() {
		return personRepository.getFriends("John");
	}

}
