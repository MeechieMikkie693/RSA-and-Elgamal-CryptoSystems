package RSATools;
import java.util.*;
public class code{

    public static int p, q, e;

    // Helper function to compute GCD
    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }//method end

        // Function to compute the modular inverse using the Extended Euclidean Algorithm
    public static int modInverse(int e, int phi) {
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
        }

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

    //method to check if a number is prime
    public static boolean isPrime(int num){
        for(int i = 2; i < num; i++){
            if(num % i == 0)
              return false;
        }//for end
        return true;
    }//method end

//To generate random numbers between 11 and 999 ( 2 to 3 digit varying random prime numbers)
 public static int generateRandomPrime(Random rand) {
        int num;
        do {
            num = rand.nextInt(989) + 11; // Generates numbers from 11 to 999
        } while (!isPrime(num));
        return num;
    }


   //To calculate the value n (product of two randomly chosen prime numbers p and q)
    public static void generatePQ(){
         Random rand = new Random();

        do {
            p = generateRandomPrime(rand);
            q = generateRandomPrime(rand);
        } while (p == q);  // Ensure p and q are distinct

        int n = p * q;
        System.out.println("Generated primes: p = " + p + ", q = " + q);
        System.out.println("n = " + n);
    }//method end

    //Method to generate 'e' (by taking phi(n) as the input)
        public static int generateE(int phi) {
        Random rand = new Random();
        int e;
        
        do {
            e = rand.nextInt(phi - 2) + 2; // Generates e in the range 2 to phi(n)-1
        } while (gcd(e, phi) != 1);
        
        return e;
    }//method end

    //Returns the private key component 'd'
    public static int keyGeneration(){
      
    //generates two random prime integers p and q
       generatePQ();

    //Calculating the Euler's totient of n=pq (which is phi(n) = (p-1)*(q-1))
       int phin = (p-1)*(q-1);

    //Generates a random integer e such that 1 < e < phi(n) and gcd(e, phi(n)) = 1
    e = generateE(phin);

    System.out.println("Generated public key: e = " + e);

    //Calculating d (private key) which is the modular multiplicative inverse of e(mod phi(n))
    int d = modInverse(e, phin);
    System.out.println("Generated private key component: d = " + d);

    System.out.println("Resulting PU: {e, n} is: { " + e + ", " + (p*q) + " }");
    System.out.println("Resulting PR: {d, n} is: { " + d + ", " + (p*q) + " }");

    return d;

    }//method end


    // Function to encode a character based on the given scheme (a-z) -> (00-25), (A-Z) -> (26-51), (0-9) -> (52-61), Space -> 62
    public static int encodeChar(char ch) {
        if (ch >= 'a' && ch <= 'z') return ch - 'a'; // 00-25
        if (ch >= 'A' && ch <= 'Z') return ch - 'A' + 26; // 26-51
        if (ch >= '0' && ch <= '9') return ch - '0' + 52; // 52-61
        if (ch == ' ') return 62; // Space -> 62
        return -1; // Invalid character
    }//method end

    // Function to decode a number back to its character
    public static char decodeCharacter(int num) {
        if (num >= 0 && num <= 25) {
            return (char) ('a' + num);  // 'a' to 'z'
        } else if (num >= 26 && num <= 51) {
            return (char) ('A' + (num - 26));  // 'A' to 'Z'
        } else if (num >= 52 && num <= 61) {
            return (char) ('0' + (num - 52));  // '0' to '9'
        } else if (num == 62) {
            return ' ';  // Space
        }
        return '?';  // Invalid character
    }//method end

    // Function to encode a string
    public static List<Integer> encodeText(String text) {
        List<Integer> encodedValues = new ArrayList<>();
        for (char ch : text.toCharArray()) {
            int encoded = encodeChar(ch);
            if (encoded != -1) {
                encodedValues.add(encoded);
            }
        }
        return encodedValues;
    }//method end

    // Function to determine the max block size
    public static int getMaxBlockSize(int n) {
        int maxEncodedValue = 62; // Highest encoding value
        int blockSize = 1;
        int value = maxEncodedValue;

        while (value * 100 + maxEncodedValue < n) { // Ensure next addition doesn't exceed n
            value = value * 100 + maxEncodedValue; // Simulate adding another encoded value
            blockSize++;
        }
        return blockSize;
    }//method end

    // Function to divide the encoded values into uniform blocks
    public static List<String> createBlocks(List<Integer> encodedValues, int n) {
        List<String> blocks = new ArrayList<>();
        int blockSize = getMaxBlockSize(n); // Get max numbers per block
        System.out.println("The max number of characters per block is: " + blockSize);

        StringBuilder block = new StringBuilder();
        for (int i = 0; i < encodedValues.size(); i++) {
            block.append(String.format("%02d", encodedValues.get(i))); // Ensure two-digit format

            if ((i + 1) % blockSize == 0 || i == encodedValues.size() - 1) {
                blocks.add(block.toString());
                block.setLength(0); // Reset block
            }
        }//for end
        return blocks;
    }

    //Function to process the given string and convert it into blocks of equal size(just before encryption)
    public static List<String> processString(String text, int n) {

        //First encode the text as per the mentioned scheme
        //(a-z) -> (00-25), (A-Z) -> (26-51), (0-9) -> (52-61), Space -> 62
        List<Integer> encodedValues = encodeText(text);

        //To determine the plain text block size (i.e. number of characters per block)
        //int n = p * q;
        System.out.println("The value of n(=p*q) is(in processString(text) after encoding text): " + n);

        //Divide the encoded values into uniform blocks
        List<String> blocks = createBlocks(encodedValues, n);
        System.out.println("The blocks of text are: " + blocks);

        return blocks;

    }//method end


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

    // Function to encrypt each plaintext block using C = M^e mod n
    public static List<Integer> encryptBlocks(List<String> plainBlocks, int e, int n) {
        List<Integer> cipherBlocks = new ArrayList<>();
        for (String M : plainBlocks) {
            int m = Integer.parseInt(M);
            int C = fastExp(m, e, n); // Encrypt using RSA formula
            cipherBlocks.add(C);
        }
        return cipherBlocks;
    }//method end


    public static List<Integer> encryption(String text, int e, int n){
        
        List<String> blocks = processString(text, n);

        //System.out.println("The values of p and q are: " + p + ", " + q);
        List <Integer> cipherBlocks = encryptBlocks(blocks, e, n);

        System.out.println("Encrypted blocks: " + cipherBlocks);
        return cipherBlocks;
    }//method end

    // Function to decrypt each cipher block using M = C^d mod n
    public static List<Integer> decryptBlocks(List<Integer> cipherBlocks, int d, int n) {
        List<Integer> plainBlocks = new ArrayList<>();
        for (int C : cipherBlocks) {
            int M = fastExp(C, d, n); // Decrypt using RSA formula
            plainBlocks.add(M);
        }
        return plainBlocks;
    }//method end

    // Function to decode a plaintext block into characters
    public static String decodePlaintextBlocks(List<Integer> plainBlocks) {
        StringBuilder decodedMessage = new StringBuilder();

        for (int block : plainBlocks) {
            // Extract individual encoded values from the block
            Stack<Integer> values = new Stack<>();
            while (block > 0) {
                values.push(block % 100);  // Extract last two digits
                block /= 100;  // Remove last two digits
            }

            // Convert extracted values to characters
            while (!values.isEmpty()) {
                decodedMessage.append(decodeCharacter(values.pop()));
            }
        }
        return decodedMessage.toString();
    }//method end

    public static String decryption(List<Integer> cipherBlocks, int e, int p, int q){

    System.out.println("The value of e, p and q are : " + e + ", " + p + ", " + q);
    //get the private key component
    int d = modInverse(e, (p-1)*(q-1));
    
    //decrypt the cipher blocks
    List<Integer> plainBlocks = decryptBlocks(cipherBlocks, d, p*q);

    //decode the plain blocks
    String plainText = decodePlaintextBlocks(plainBlocks);
    //System.out.println("Decrypted text: " + plainText);
    return plainText;

    }//method end
}//class end