package dao;

import models.FoodType;
import models.Restaurant;

import java.util.List;

public interface FoodTypeDao {

  //create
  void add(FoodType foodType); // N
  void addFoodTypeToRestaurant(FoodType foodtype, Restaurant restaurant);

  //read
  List<FoodType> getAll(); // we may need this in the future. Even though it does not 100% match a specific user story, it should be implemented so we can retrieve all Foodtypes, for example to programmatically generate some UI.
  List<Restaurant> getAllRestaurantsForAFoodtype(int id);

  //update
  //omit for now

  //delete
  void deleteById(int id);

}
