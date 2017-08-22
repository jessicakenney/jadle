package dao;
import dao.Sql2oReviewDao;
import models.Review;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import static org.junit.Assert.*;

public class Sql2oReviewDaoTest {

  private Sql2oReviewDao reviewDao;
  // private Sql2oAttendeeDao attendeeDao;
  private Connection conn;

  public Review getTestReview() {
    String writtenBy = "Jessica Sheridan";
    int rating = 4;
    int restaurantId = 1;
    return new Review(writtenBy, rating, restaurantId);
  }

  @Before
  public void setUp() throws Exception {
    String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
    Sql2o sql2o = new Sql2o(connectionString, "", "");
    reviewDao = new Sql2oReviewDao(sql2o);
    conn = sql2o.open();
  }

  @After
  public void tearDown() throws Exception {
    conn.close();
  }

  @Test
  public void addingReviewSetsId() throws Exception {
    Review review = getTestReview();
    reviewDao.add(review);
    assertEquals(1, review.getId());
  }

  @Test
  public void deleteById_deletesVeryWell() {
    Review review = getTestReview();
    reviewDao.add(review);
    reviewDao.deleteById(review.getId());
    //need a better test here
    //assertEquals(0, reviewDao.getAll().size());
  }

  @Test
  public void getAllReviewsByRestaurantWorksWell(){
    Review review = getTestReview();
    Review review2 = getTestReview();
    int id = review.getRestaurantId();
    reviewDao.add(review);
    reviewDao.add(review2);
    assertEquals(2,reviewDao.getAllReviewsByRestaurant(id).size());
  }

}



