package com.kspichale.neo4j;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface PersonRepository extends GraphRepository<Person> {

	@Query(value = "start person=node:peoplesearch(name={0}) match person<-[:friends]->afriend return afriend")
	Iterable<Person> getFriends(String name);
}
