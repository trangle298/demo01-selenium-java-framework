package helpers.providers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for selecting random samples from a list.
 * Provides methods to get random samples of specified size or within a range.
 */
public class RandomSampleProvider {

    private static final Logger LOG = LogManager.getLogger(RandomSampleProvider.class);

    /**
     * Get a random sample of items from the original list.
     * If the requested sample size exceeds the list size, returns the entire list.
     *
     * @param originalList The original list to sample from
     * @param sampleSize   The number of random items to select
     * @param <T>          The type of items in the list
     * @return A list containing the random sample
     */
    public static <T> List <T> getRandomSamplesFromList(List<T> originalList, int sampleSize) {
        int actualSize = Math.min(sampleSize, originalList.size());

        if (actualSize == originalList.size()) {
            LOG.info("Requested sample size ({}) >= available items ({}). Returning all available items.", sampleSize, originalList.size());
            return new ArrayList<>(originalList);
        }

        Random random = new Random();
        List<T> sample = new ArrayList<>();
        List<T> pool = new ArrayList<>(originalList);

        for (int i = 0; i < actualSize; i++) {
            int randomIndex = random.nextInt(pool.size());
            sample.add(pool.remove(randomIndex));
        }

        LOG.info("Selected {} random item from {} available: {}", actualSize, originalList.size(), sample);
        return sample;
    }

   /**
     * Get a random sample of items from the original list within the specified quantity range.
     *
     * @param originalList The original list to sample from
     * @param minQuan      Minimum number of items to select
     * @param maxQuan      Maximum number of items to select
     * @param <T>          The type of items in the list
     * @return A list containing the random sample
     */
    public static <T> List <T> getRandomSamplesFromList(List<T> originalList, int minQuan, int maxQuan) {
        // Determine the maximum number of samples that can be selected
        int maxSize = Math.min(originalList.size(), maxQuan);

        // Ensure minQuan < maxQuan
        if (minQuan > maxQuan) {
            throw new IllegalArgumentException("minQuan should not be greater than maxQuan");
        }

        // Get a random size within the specified range
        int randomSize = RandomSampleProvider.getRandomIntInRange(minQuan, maxSize);

        // Select random seats based on the determined size
        List<T> samples = RandomSampleProvider.getRandomSamplesFromList(originalList, randomSize);

        return samples;
    }

    /**
     * Get a single random item from the original list.
     *
     * @param originalList The original list to sample from
     * @param <T>          The type of items in the list
     * @return A single random item
     */
    public static <T> T getRandomSampleFromList(List<T> originalList) {
        return getRandomSamplesFromList(originalList, 1).get(0);
    }

    /**
     * Get a random integer within the specified inclusive range [min, max].
     *
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return Random integer within the range
     */
    public static Integer getRandomIntInRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
}