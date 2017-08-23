/**
 * Created by Guest on 8/22/17.
 */

import com.google.gson.Gson;
        import dao.Sql2oFoodTypeDao;
        import dao.Sql2oRestaurantDao;
import dao.Sql2oReviewDao;
import models.ApiException;
import models.FoodType;
import models.Restaurant;
import models.Review;
import org.sql2o.Connection;
        import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class App {

    public static void main(String[] args) {
        Sql2oFoodTypeDao foodTypeDao;
        Sql2oRestaurantDao restaurantDao;
        Sql2oReviewDao reviewDao;
        Connection conn;
        Gson gson = new Gson();

        String connectionString = "jdbc:h2:~/jadle.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'"; //check me!

        Sql2o sql2o = new Sql2o(connectionString, "", "");
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodTypeDao = new Sql2oFoodTypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open();


        post("/restaurants/new", "application/json", (req, res) -> {
            Restaurant restaurant = gson.fromJson(req.body(), Restaurant.class);
            restaurantDao.add(restaurant);
            res.status(201);
            return gson.toJson(restaurant);
        });

        get("/restaurants", "application/json", (req, res) -> { //accept a request in format JSON from an app
            return gson.toJson(restaurantDao.getAll());//send it back to be displayed
        });

        get("/restaurants/:id", "application/json", (req, res) -> { //accept a request in format JSON from an app
            int restaurantId = Integer.parseInt(req.params("id"));
            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            if (restaurantToFind == null){
                throw new ApiException(404, String.format("No restaurant with id: %d exists", restaurantId));
            }
            return gson.toJson(restaurantToFind);
        });

        // New Review
        post("/restaurants/:restaurantId/reviews/new", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("restaurantId"));
            Review review = gson.fromJson(req.body(), Review.class);
            review.setRestaurantId(restaurantId); //why do I need to set separately?
            reviewDao.add(review);
            res.status(201);
            return gson.toJson(review);
        });

        //Get all reviews by restaurant
        get("/restaurants/:restaurantId/reviews", "application/json", (req,res)-> {
            int restaurantId = Integer.parseInt(req.params("restaurantId"));
            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            if (restaurantToFind == null){
                throw new ApiException(404, String.format("No restaurant with id: %d exists", restaurantId));
            }
            if (reviewDao.getAllReviewsByRestaurant(restaurantId).size() == 0) {
                throw new ApiException(404, String.format("No reviews for this restaurant exist with id: %d exists", restaurantId));
            }
            return gson.toJson(reviewDao.getAllReviewsByRestaurant(restaurantId));
        });


        //Food Type
        post("/foodtypes/new", "application/json", (req,res)-> {
            FoodType foodType = gson.fromJson(req.body(), FoodType.class);
            foodTypeDao.add(foodType);
            res.status(201);
            return gson.toJson(foodType);
        });

        // add FoodTypeToRestaurantForaFoodType not sure here about how to handle foodType
        // do we need get FoodTypeById ?
        post("/foodtypes/:foodTypeId/restaurants/:restaurantId", "application/json", (req,res)-> {
            FoodType foodType = gson.fromJson(req.body(), FoodType.class);
            foodTypeDao.add(foodType);
            int restaurantId = Integer.parseInt(req.params("restaurantId"));
            Restaurant restaurant = restaurantDao.findById(restaurantId);
            foodTypeDao.addFoodTypeToRestaurant(foodType,restaurant);
            res.status(201);
            return gson.toJson(foodType);
        });

        // getAll foodtypes
        get("/foodtypes", "application/json", (req,res) -> {
            return gson.toJson(foodTypeDao.getAll());
        });

        //getAllRestaurantsForAFoodType
        get("/foodtypes/:id/restaurants", (req,res) -> {
            int foodTypeId = Integer.parseInt(req.params("id"));
            return gson.toJson(foodTypeDao.getAllRestaurantsForAFoodtype(foodTypeId));
        });

        exception(ApiException.class, (errorObjectThatWeMade, req, res) -> {
            ApiException error = (ApiException) errorObjectThatWeMade;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", error.getStatusCode());
            jsonMap.put("errorMessage", error.getMessage());
            res.type("application/json");
            res.status(error.getStatusCode());
            res.body(gson.toJson(jsonMap));
        });

        //Filter
        after((req,res)->{
            res.type("application/json");

        });



    }
}