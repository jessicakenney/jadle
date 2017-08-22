package dao;


import models.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oReviewDao implements ReviewDao {
  private final Sql2o sql2o;

  public Sql2oReviewDao(Sql2o sql2o){
    this.sql2o = sql2o;
  }

  @Override
  public void add(Review review) {
    String sql = "INSERT INTO reviews (writtenby,rating,restaurantid) VALUES (:writtenby, :rating,:restaurantid)"; //raw sql
    try(Connection con = sql2o.open()){ //try to open a connection
      int id = (int) con.createQuery(sql) //make a new variable
              .addParameter("writtenby", review.getWrittenBy())
              .addParameter("rating", review.getRating())
              .addParameter("restaurantid", review.getRestaurantId())
              .addColumnMapping("WRITTENBY", "writtenby")
              .addColumnMapping("RATING", "rating")
              .addColumnMapping("RESTAURANTID", "restaurantid")
              .executeUpdate() //run it all
              .getKey(); //int id is now the row number (row “key”) of db
      review.setId(id); //update object to set id now from database
    } catch (Sql2oException ex) {
      System.out.println(ex); //oops we have an error!
    }
  }

  @Override
  public List<Review> getAllReviewsByRestaurant(int restaurantId) {
    try(Connection con = sql2o.open()){
      return con.createQuery("SELECT * FROM reviews WHERE restaurantId = :restaurantId")
              .addParameter("restaurantId", restaurantId)
              .executeAndFetch(Review.class);
    }
  }

  @Override
  public void deleteById(int id) {
    String sql = "DELETE from reviews WHERE id=:id"; //raw sql
    try (Connection con = sql2o.open()) {
      con.createQuery(sql)
              .addParameter("id", id)
              .executeUpdate();
    } catch (Sql2oException ex){
      System.out.println(ex);
    }
  }

}



