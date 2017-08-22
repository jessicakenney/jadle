package dao;
        import dao.Sql2oFoodTypeDao;
        import dao.Sql2oRestaurantDao;
        import models.FoodType;
        import models.Restaurant;
        import org.junit.After;
        import org.junit.Before;
        import org.junit.Test;
        import org.sql2o.Connection;
        import org.sql2o.Sql2o;
        import org.sql2o.Sql2oException;

        import static org.junit.Assert.*;

public class Sql2oFoodTypeDaoTest {

  private Sql2oFoodTypeDao foodTypeDao;
  private Sql2oRestaurantDao restaurantDao;
  private Connection conn;

  public FoodType getTestFoodType() {
    String name = "Mexican";
    return new FoodType(name);
  }

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
    foodTypeDao = new Sql2oFoodTypeDao(sql2o);
    restaurantDao = new Sql2oRestaurantDao(sql2o);
    conn = sql2o.open();
  }

  @After
  public void tearDown() throws Exception {
    conn.close();
  }

  @Test
  public void addingFoodTypeSetsId() throws Exception {
    FoodType foodType = getTestFoodType();
    foodTypeDao.add(foodType);
    assertEquals(1, foodType.getId());
  }

  @Test
  public void deleteById_deletesVeryWell() {
    FoodType foodType = getTestFoodType();
    foodTypeDao.add(foodType);
    foodTypeDao.deleteById(foodType.getId());
   assertEquals(0, foodTypeDao.getAll().size());
  }

  @Test
  public void getAll_worksCorrectly(){
    FoodType foodType = getTestFoodType();
    FoodType foodType2 = getTestFoodType();
    foodTypeDao.add(foodType);
    foodTypeDao.add(foodType2);
    assertEquals(2,foodTypeDao.getAll().size());
  }

  @Test
  public void addFoodTypeToRestaurantAddsTypeCorrectly() throws Exception {
    Restaurant testRestaurant = getTestRestaurant();
    Restaurant altRestaurant = getTestRestaurant2();
    restaurantDao.add(testRestaurant);
    restaurantDao.add(altRestaurant);
    FoodType testFoodType = getTestFoodType();
    foodTypeDao.add(testFoodType);
    foodTypeDao.addFoodTypeToRestaurant(testFoodType, testRestaurant);
    foodTypeDao.addFoodTypeToRestaurant(testFoodType, altRestaurant);
    assertEquals(2, foodTypeDao.getAllRestaurantsForAFoodtype(testFoodType.getId()).size());
  }

  @Test
  public void deleteingRestaurantAlsoUpdatesJoinTable() throws Exception {
    FoodType testFoodType  = new FoodType("Seafood");
    foodTypeDao.add(testFoodType);

    Restaurant testRestaurant = getTestRestaurant();
    restaurantDao.add(testRestaurant);

    Restaurant altRestaurant = getTestRestaurant2();
    restaurantDao.add(altRestaurant);

    restaurantDao.addRestaurantToFoodType(testRestaurant,testFoodType);
    restaurantDao.addRestaurantToFoodType(altRestaurant, testFoodType);

    restaurantDao.deleteById(testRestaurant.getId());
    assertEquals(0, restaurantDao.getAllFoodtypesForARestaurant(testRestaurant.getId()).size());
  }

}


