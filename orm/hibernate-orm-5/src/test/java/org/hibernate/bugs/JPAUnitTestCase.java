package org.hibernate.bugs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {
	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory("templatePU");
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		Map<String, Car> customizations = new HashMap<>();
		customizations.put("GER", carWithColor("green"));
		customizations.put("USA", carWithColor("blue"));

		RaceDriver raceDriver = new RaceDriver();
		raceDriver.setCars(customizations);

		entityManager.persist(raceDriver);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private Car carWithColor(String color) {
		Engine engine = new Engine();
		engine.setHorsePower(color.hashCode());

		Car car = new Car();
		car.setColor(color);
		car.setEngine(engine);
		return car;
	}

	@Entity
	public static class RaceDriver {
		@Id
		@GeneratedValue
		Long id;

		@MapKeyColumn(name = "location")
		@ElementCollection(fetch = FetchType.EAGER)
		@CollectionTable(name = "cars", joinColumns = {@JoinColumn(name = "driver_id", referencedColumnName = "id")})
		private Map<String, Car> cars;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Map<String, Car> getCars() {
			return cars;
		}

		public void setCars(Map<String, Car> cars) {
			this.cars = cars;
		}
	}

	@Embeddable
	public static class Car {
		//        @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
		@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
		private Engine engine;

		private String color;

		public Engine getEngine() {
			return engine;
		}

		public void setEngine(Engine engine) {
			this.engine = engine;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}
	}

	@Entity
	public static class Engine {
		@Id
		@GeneratedValue
		private Integer id;
		private Integer horsePower;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Integer getHorsePower() {
			return horsePower;
		}

		public void setHorsePower(Integer horsePower) {
			this.horsePower = horsePower;
		}
	}
}
