package dao;


import models.FoodType;
import models.Restaurant;
import models.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.ArrayList;
import java.util.List;

public class Sql2oFoodTypeDao implements FoodTypeDao {
  private final Sql2o sql2o;

public Sql2oFoodTypeDao(Sql2o sql2o) {
  this.sql2o = sql2o;
}


  @Override
  public void add(FoodType foodType) {
    String sql = "INSERT INTO foodtypes (name) VALUES (:name)"; //raw sql
    try(Connection con = sql2o.open()){ //try to open a connection
      int id = (int) con.createQuery(sql) //make a new variable
              .addParameter("name", foodType.getName())
              .addColumnMapping("NAME", "foodtype")
              .executeUpdate() //run it all
              .getKey(); //int id is now the row number (row “key”) of db
      foodType.setId(id); //update object to set id now from database
    } catch (Sql2oException ex) {
      System.out.println(ex);
    }
  }

 @Override
 public List<FoodType> getAll(){
    try(Connection con = sql2o.open()){
      return con.createQuery("SELECT * FROM foodtypes")
              .executeAndFetch(FoodType.class);
    }
  }

  @Override
  public void deleteById(int id) {
    String sql = "DELETE from foodtypes WHERE id=:id"; //raw sql
    String deleteJoin = "DELETE from restaurants_foodtypes WHERE foodtypeid=:foodtypeid"; //raw sql
    try (Connection con = sql2o.open()) {
      con.createQuery(sql)
              .addParameter("id", id)
              .executeUpdate();
      con.createQuery(deleteJoin)
              .addParameter("foodtypeid", id)
              .executeUpdate();
    } catch (Sql2oException ex){
      System.out.println(ex);
    }
  }

  @Override
  public void addFoodTypeToRestaurant(FoodType foodType, Restaurant restaurant){
    String sql = "INSERT INTO restaurants_foodtypes (restaurantid, foodtypeid) VALUES (:restaurantId, :foodtypeId)";
    try (Connection con = sql2o.open()) {
      con.createQuery(sql)
              .addParameter("restaurantId", restaurant.getId())
              .addParameter("foodtypeId", foodType.getId())
              .executeUpdate();
    } catch (Sql2oException ex){
      System.out.println(ex);
    }
  }

  @Override
  public List<Restaurant> getAllRestaurantsForAFoodtype(int foodTypeId) {

    ArrayList<Restaurant> restaurants = new ArrayList<>();
    String joinQuery = "SELECT restaurantid FROM restaurants_foodtypes WHERE foodtypeid = :foodtypeId";

    try (Connection con = sql2o.open()) {
      List<Integer> allRestaurantIds = con.createQuery(joinQuery)
              .addParameter("foodtypeId", foodTypeId)
              .executeAndFetch(Integer.class); //what is happening in the lines above? gettingrestIds
      for (Integer restaurantId : allRestaurantIds){
        String restaurantQuery = "SELECT * FROM restaurants WHERE id = :restaurantId";
        restaurants.add(
                con.createQuery(restaurantQuery)
                        .addParameter("restaurantId", restaurantId)
                        .executeAndFetchFirst(Restaurant.class));
      } //why are we doing a second sql query - set? first gets the ids the next actually gets the objects
    } catch (Sql2oException ex){
      System.out.println(ex);
    }
    return restaurants;
  }



}
