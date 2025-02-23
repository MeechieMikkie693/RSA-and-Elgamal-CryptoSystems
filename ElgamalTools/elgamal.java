package ElgamalTools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class elgamal {
    public static int q, alpha;

    //method to check if a number is prime
    public static boolean isPrime(int num){
        for(int i = 2; i < num; i++){
            if(num % i == 0)
              return false;
        }//for end
        return true;
    }//method end

//Function to generate prime numbe q ka random value
 public static int generateRandomPrime(Random rand) {
        int num;
        do {
            num = rand.nextInt(100);
        } while (!isPrime(num));
        return num;
    }//method end

    // Utility Function to check if a number is a primitive root of q
    public static boolean isPrimitiveRoot(int g, int q) {
        Set<Integer> set = new HashSet<>();
        int value = 1;

        for (int i = 1; i < q; i++) {
            value = (value * g) % q;
            if (set.contains(value)) {
                return false;
            }
            set.add(value);
        }

        return set.size() == q - 1;
    }//method end

      //Utility Function to find all primitive roots of q
    public static List<Integer> findPrimitiveRoots(int q) {
        List<Integer> primitiveRoots = new ArrayList<>();
        for (int i = 2; i < q; i++) {
            if (isPrimitiveRoot(i, q)) {
                primitiveRoots.add(i);
            }
        }
        return primitiveRoots;
    }//method end

    //Function to assign a value to the primitive root alpha
    public static void assignPrimitiveRoot(int q) {
        List<Integer> primitiveRoots = findPrimitiveRoots(q);

        if(!primitiveRoots.isEmpty()){
            Random random = new Random();
            int randomIndex = random.nextInt(primitiveRoots.size());
            alpha = primitiveRoots.get(randomIndex);

            System.out.println("Primitive root alpha: " + alpha);
        }//if end
        else{
            System.out.println("No primitive roots found for " + q);
        }//else end
    }//method end

//Function to calculate the modular exponentiation
        public static int fastExp(int base, int pow, int n){
            int f = 1;
            String b = Integer.toBinaryString(pow);
            for(int i = 0; i < b.length(); i++){
              f = (f*f) % n;
              if(b.charAt(i) == '1')
                f = (f*base) % n;
            }//for end
            return f;
    }//method end

//This function returns private and public key
    public static int[] publicKeyGeneration(){

        //Generate a random prime number q
        Random rand = new Random();
        q = generateRandomPrime(rand);
        System.out.println("Generated prime number: q = " + q);

        //Assign a value to the primitive root alpha
        assignPrimitiveRoot(q);

        //Generate a random private key value X which is 1 < X < q-1
         Random random = new Random();
         int privateKey = random.nextInt(q-3) + 2;
         System.out.println("The generated private key is: " + privateKey);

        //Calculate the public key value Y = alpha^X mod q
        int publicKey = fastExp(alpha, privateKey, q);

        System.out.println("Generated public key: Y = " + publicKey);

        return new int[]{privateKey, publicKey};
    }//method end


// Function to generate Message and small value k
    public static int generateRandomNumberLessthanQ(int q){
        Random rand = new Random();
            if (q <= 2) {
        throw new IllegalArgumentException("q must be greater than 2");
    }//end if
    System.out.println("Generating random number with q = " + q);
    return rand.nextInt(q-1) + 1;
    }//method end

    public static int[] encryption(int M, int publicKey, int q, int alpha){
       //Select a random integer k
       int k = generateRandomNumberLessthanQ(q);

       //Calculate one time key K
       int K  = fastExp(publicKey, k, q);

       //Calculate C1 = alpha^k mod q
       int C1 = fastExp(alpha, k, q);

       //Calculate C2 = M * K mod q
       int C2 = (M * K) % q;

       return new int[]{C1, C2};
    }//method end

//Function to calculate modInverse of a given number
public static int modInverse(int e, int phi){
    int t = 0, newT = 1;
    int r = phi, newR = e;

    while (newR != 0) {
            int quotient = r / newR;

            // Update t and newT
            int tempT = t;
            t = newT;
            newT = tempT - quotient * newT;

            // Update r and newR
            int tempR = r;
            r = newR;
            newR = tempR - quotient * newR;
        }//while end

        // If r > 1, then e has no modular inverse
        if (r > 1) {
            throw new ArithmeticException("e has no modular inverse mod phi(n)");
        }

        // Ensure d is positive
        if (t < 0) {
            t += phi;
        }

        return t;

}//method end

//Function to decrypt the message 
    public static int decryption(int[] C, int privateKey){
        
        //Use C1 to get back the one-time key K
        int K = fastExp(C[0], privateKey, q);

        //Calculate modInverse of K
        int inverseK = modInverse(K, q);

        //Calculate M = C2 * K^-1 mod q
        int M = (C[1] * inverseK) % q;

        return M;

    }//method end



}//class end
