package com.kspichale.neo4j;

import java.io.File;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.Traversal;

public class Neo4jTest {

	private static final String DB_PATH = "neo4j-shortest-path";
	private static GraphDatabaseService graphDb;
	private static Index<Node> indexService;

	public static void main(final String[] args) {
		deleteFileOrDirectory(new File(DB_PATH));
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		indexService = graphDb.index().forNodes("nodes");
		registerShutdownHook();
		Transaction tx = graphDb.beginTx();
		try {

			Node bobNode = graphDb.createNode();
			bobNode.setProperty("name", "Bob");
			indexService.add(bobNode, "name", "Bob");

			Node mikeNode = graphDb.createNode();
			mikeNode.setProperty("name", "Mike");
			indexService.add(mikeNode, "name", "Mike");

			Node alexNode = graphDb.createNode();
			alexNode.setProperty("name", "Alex");
			indexService.add(alexNode, "name", "Alex");

			DynamicRelationshipType rel = DynamicRelationshipType.withName("KNOWS");

			bobNode.createRelationshipTo(mikeNode, rel);
			mikeNode.createRelationshipTo(alexNode, rel);

			tx.success();
		} finally {
			tx.finish();
		}

		Node bobNode = indexService.get("name", "Bob").getSingle();
		Node alexNode = indexService.get("name", "Alex").getSingle();

		DynamicRelationshipType rel = DynamicRelationshipType.withName("KNOWS");
		PathFinder<Path> finder = GraphAlgoFactory.shortestPath(
				Traversal.expanderForTypes(DynamicRelationshipType.withName("KNOWS"), Direction.BOTH), 4);
		Path foundPath = finder.findSinglePath(bobNode, alexNode);

		System.out.println("Path from Bob to Alex: " + Traversal.simplePathToString(foundPath, "name"));

		System.out.println("Shutting down database ...");
		graphDb.shutdown();
	}

	private static void registerShutdownHook() {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	private static void deleteFileOrDirectory(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File child : file.listFiles()) {
					deleteFileOrDirectory(child);
				}
			}
			file.delete();
		}
	}

}