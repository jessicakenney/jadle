package dao;

import models.FoodType;
import models.Restaurant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.Arrays;

import static org.junit.Assert.*;

public class Sql2oRestaurantDaoTest {


  private Sql2oRestaurantDao restaurantDao;
  private Sql2oFoodTypeDao foodTypeDao;
  private Connection conn;

  public Restaurant getTestRestaurant() {
    String name = "ChaChaCha";
    String address = "123 NE Fremont Street";
    String zipcode = "97213";
    String phone = "503-123-1234";
    String website = "www.chachacha.com";
    String email = "chachacha@gmail.com";
    String image = "/chachacha.jpeg";
    return new Restaurant(name, address, zipcode, phone, website, email, image);
  }

  public Restaurant getTestRestaurant2() {
    String name = "Killer Burger";
    String address = "123 NE Sandy Blvd";
    String zipcode = "97211";
    String phone = "503-769-3333";
    String website = "www.killerburger.com";
    String email = "killerburger@gmail.com";
    String image = "/killerburger.jpeg";
    return new Restaurant(name, address, zipcode, phone, website, email, image);
  }

  @Before
  public void setUp() throws Exception {
    String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
    Sql2o sql2o = new Sql2o(connectionString, "", "");
    restaurantDao = new Sql2oRestaurantDao(sql2o);
    foodTypeDao = new Sql2oFoodTypeDao(sql2o);
    conn = sql2o.open();
  }

  @After
  public void tearDown() throws Exception {
    conn.close();
  }

  @Test
  public void addingEventSetsId() throws Exception {
    Restaurant restaurant = getTestRestaurant();
    restaurantDao.add(restaurant);
    assertEquals(1, restaurant.getId());
  }

  @Test
  public void existingRestaurantsCanBeFoundById() throws Exception {
    Restaurant restaurant = getTestRestaurant();
    restaurantDao.add(restaurant);
    Restaurant foundRestaurant = restaurantDao.findById(restaurant.getId());
    assertEquals(restaurant, foundRestaurant);
  }

  @Test
  public void getAll_allRestaurantsAreFound() throws Exception {
    Restaurant restaurant = getTestRestaurant();
    Restaurant anotherRestaurant = getTestRestaurant2();
    restaurantDao.add(restaurant);
    restaurantDao.add(anotherRestaurant);
    int number = restaurantDao.getAll().size();
    assertEquals(2, number);
  }

  @Test
  public void getAll_noRestaurantsAreFound() throws Exception {
    int number = restaurantDao.getAll().size();
    assertEquals(0, number);
  }

  @Test
  public void update_correctlyUpdates() {
    Restaurant restaurant = getTestRestaurant();
    restaurantDao.add(restaurant);
    restaurantDao.update(restaurant.getId(), "newName", "newAddress", "newZipcode", "newPhone", "newWebsite", "newEmail", "newImage");
    Restaurant updatedRestaurant = restaurantDao.findById(restaurant.getId());
    assertEquals("newZipcode", updatedRestaurant.getZipcode());
    assertEquals("newName", updatedRestaurant.getName());
    assertEquals("newAddress", updatedRestaurant.getAddress());
    assertEquals("newPhone", updatedRestaurant.getPhone());
  }

  @Test
  public void deleteById_deletesVeryWell() {
    Restaurant restaurant = getTestRestaurant();
    restaurantDao.add(restaurant);
    restaurantDao.deleteById(restaurant.getId());
    assertEquals(0,restaurantDao.getAll().size());
  }

  @Test
  public void getAllFoodtypesForARestaurantReturnsFoodtypesCorrectly() throws Exception {
    FoodType testFoodType  = new FoodType("Seafood");
    foodTypeDao.add(testFoodType);
    FoodType otherFoodType  = new FoodType("Bar Food");
    foodTypeDao.add(otherFoodType);
    Restaurant testRestaurant = getTestRestaurant();
    restaurantDao.add(testRestaurant);
    restaurantDao.addRestaurantToFoodType(testRestaurant,testFoodType);
    restaurantDao.addRestaurantToFoodType(testRestaurant,otherFoodType);

    FoodType[] foodTypes = {testFoodType, otherFoodType}; //oh hi what is this?

    assertEquals(restaurantDao.getAllFoodtypesForARestaurant(testRestaurant.getId()), Arrays.asList(foodTypes));
  }

  @Test
  public void deleteingFoodTypesAlsoUpdatesJoinTable() throws Exception {
    FoodType testFoodType  = new FoodType("Seafood");
    foodTypeDao.add(testFoodType);

    Restaurant testRestaurant = getTestRestaurant();
    restaurantDao.add(testRestaurant);

    Restaurant altRestaurant = getTestRestaurant2();
    restaurantDao.add(altRestaurant);

    restaurantDao.addRestaurantToFoodType(testRestaurant,testFoodType);
    restaurantDao.addRestaurantToFoodType(altRestaurant, testFoodType);

    restaurantDao.deleteById(testFoodType.getId());
    assertEquals(0, restaurantDao.getAllFoodtypesForARestaurant(testRestaurant.getId()).size());
  }

}

