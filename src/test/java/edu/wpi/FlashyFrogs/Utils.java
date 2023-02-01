package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.util.Random;
import lombok.NonNull;

/** A class containing static test utilities that all tests can benefit from */
public class Utils {
  /**
   * Generates a random string of the given length. The string will be completely alphabetical, with
   * a mix of upper and lower case
   *
   * @param length the length to generate the string at
   * @param randomGenerator the random number generator to use
   * @return the generated string
   */
  @NonNull
  public static String generateRandomString(int length, @NonNull Random randomGenerator) {
    // Use a random number generator stream, generate from lowercase a to uppercase Z (right is
    // exclusive, so add 1)
    return randomGenerator
        .ints('A', 'z' + 1)
        .filter(i -> (i >= 'a' && i <= 'z') || (i >= 'A' && i <= 'Z'))
        . // Ensure that we only get alphabetic
        limit(length)
        . // Limit us to length characters
        // Collect in a string builder, initially add the int as a code point (e.g., convert to
        // char)
        // and then merge string builders with append
        collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString(); // Finally, convert the string builder to a string
  }

  /**
   * Generates a random Node, and then ensures that all of its fields match input when gotten
   *
   * @param randomGenerator the random number generator to use with generating, so that external
   *     files can call this
   * @return the randomly generated Node
   */
  @NonNull
  public static Node generateRandomNode(@NonNull Random randomGenerator) {
    String idString =
        generateRandomString(
            randomGenerator.nextInt(1, 10), randomGenerator); // Generate the ID string
    int xCoord = randomGenerator.nextInt(0, 1000); // Generate the xCoord
    int yCoord = randomGenerator.nextInt(0, 1000); // Generate the yCoord

    // Generate the floor
    Node.Floor[] floors = Node.Floor.values(); // Get the list of floors, we will pick one
    Node.Floor floor = floors[randomGenerator.nextInt(0, floors.length)]; // Get a random floor

    String buildingName =
        generateRandomString(
            randomGenerator.nextInt(15), randomGenerator); // Generate a random building

    // Create the node from what we've generated
    return new Node(idString, buildingName, floor, xCoord, yCoord);
  }

  @NonNull
  public static LocationName generateRandomLocation(@NonNull Random randomGenerator) {
    String longName =
        generateRandomString(
            randomGenerator.nextInt(10), randomGenerator); // Generate a random long name
    String shortName = generateRandomString(5, randomGenerator); // Generate a random short name

    // Generate the location type
    LocationName.LocationType[] types =
        LocationName.LocationType.values(); // Get the list of types, we will pick one
    LocationName.LocationType type =
        types[randomGenerator.nextInt(0, types.length)]; // Get a random floor

    return new LocationName(longName, type, shortName);
  }
}
