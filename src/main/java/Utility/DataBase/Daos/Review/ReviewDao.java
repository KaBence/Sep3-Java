package Utility.DataBase.Daos.Review;

import sep.DtoComment;
import sep.DtoReview;

import java.util.ArrayList;

public interface ReviewDao
{
    String createReview(DtoReview dto) throws Exception;
    ArrayList<DtoReview> getAllReviewsByFarmer(String farmer);
    String postComment(DtoComment comment) throws Exception;
    ArrayList<DtoComment> getAllCommentsByReview(String farmer,String customer);
}
