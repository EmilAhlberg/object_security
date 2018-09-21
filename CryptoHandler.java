import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.KeyFactory;

public class CryptoHandler {
  private static BigInteger g512 = new BigInteger("1234567890", 16);
  private static BigInteger p512 = new BigInteger("1234567890", 16);

  public KeyPair getKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

    DHParameterSpec dhParams = new DHParameterSpec(p512, g512);
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");

    keyGen.initialize(dhParams, new SecureRandom());

    KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH");
    KeyPair aPair = keyGen.generateKeyPair();

    return null;
  }

  public static void createSpecificKey(int pVal, int gVal) throws Exception {

    BigInteger p = new BigInteger(Integer.toString(pVal));
    BigInteger g = new BigInteger(Integer.toString(gVal));

    KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH", "BC");

    DHParameterSpec param = new DHParameterSpec(p, g);
    kpg.initialize(param);
    KeyPair kp = kpg.generateKeyPair();

    KeyFactory kfactory = KeyFactory.getInstance("DH", "BC");

    DHPublicKeySpec kspec = (DHPublicKeySpec) kfactory.getKeySpec(kp.getPublic(),
        DHPublicKeySpec.class);
  }

  public static void main(String[] args) throws  Exception {
      CryptoHandler.createSpecificKey(5, 3);
  }
}



/*
javac -sourcepath / -classpath bcprov-jdk15on-160.jar CryptoHandler.java
*/
