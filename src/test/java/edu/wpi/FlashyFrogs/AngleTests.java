package edu.wpi.FlashyFrogs;

import org.junit.jupiter.api.Test;

public class AngleTests {

  private static double magnitude(double arr[], int N) {

    // Stores the final magnitude
    double magnitude = 0;

    // Traverse the array
    for (int i = 0; i < N; i++) magnitude += arr[i] * arr[i];

    // Return square root of magnitude
    return Math.sqrt(magnitude);
  }

  // Function to find the dot
  // product of two vectors
  private static double dotProduct(double[] arr, double[] brr, int N) {

    // Stores dot product
    double product = 0;

    // Traverse the array
    for (int i = 0; i < N; i++) product = product + arr[i] * brr[i];

    // Return the product
    return product;
  }

  private static void angleBetweenVectors(double[] arr, double[] brr, int N) {

    // Stores dot product of two vectors
    double dotProductOfVectors = dotProduct(arr, brr, N);

    // Stores magnitude of vector A
    double magnitudeOfA = magnitude(arr, N);

    // Stores magnitude of vector B
    double magnitudeOfB = magnitude(brr, N);

    // Stores angle between given vectors
    double angle = dotProductOfVectors / (magnitudeOfA * magnitudeOfB);
    angle = Math.acos(angle);
    System.out.println(Math.toDegrees(angle));
  }

  @Test
  public void angleTest() {
    double[] a = {2, 0};
    double[] b = {0, 2};

    angleBetweenVectors(a, b, 2);
  }
}
