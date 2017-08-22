package dao;

import models.Review;

import java.util.List;

public interface ReviewDao {

  //create
  void add(Review review); //F

  //read
  List<Review> getAllReviewsByRestaurant(int restaurantId); // H & G

  //update
  //omit for now

  //delete
  void deleteById(int id); //M
}

