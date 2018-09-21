import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Security;
import java.security.KeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CryptoHandler {
  private static BigInteger g512 = new BigInteger("1234567890", 16);
  private static BigInteger p512 = new BigInteger("1234567890", 16);

  public KeyPair getKeyPair() {
    Security.addProvider(new  BouncyCastleProvider());

    DHParameterSpec dhParams = new DHParameterSpec(p512, g512);
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH", "BC");

    keyGen.initialize(dhParams, new SecureRandom());

    KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
    KeyPair aPair = keyGen.generateKeyPair();
  }

  public void placeHolder() {
    KeyAgreement bKeyAgree = KeyAgreement.getInstance("DH", "BC");
    KeyPair bPair = keyGen.generateKeyPair();

    aKeyAgree.init(aPair.getPrivate());
    bKeyAgree.init(bPair.getPrivate());

    aKeyAgree.doPhase(bPair.getPublic(), true);
    bKeyAgree.doPhase(aPair.getPublic(), true);

    MessageDigest hash = MessageDigest.getInstance("SHA1", "BC");
    System.out.println(new String(hash.digest(aKeyAgree.generateSecret())));
    System.out.println(new String(hash.digest(bKeyAgree.generateSecret())));
  }

  public static void createSpecificKey(int pVal, int gVal) throws Exception {
    Security.addProvider(new BouncyCastleProvider());

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
}
