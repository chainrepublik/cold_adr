// Author : Vlad Cristian
// Contact : vcris@gmx.com

package adr_generator;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.ResultSet;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;



public class CAddress 
{
	 
    // Public and private key
    public byte[] public_key;
    
    // Private key
    public byte[] private_key;
    
    // Description
    public String description="";
    
    // Public key
    ECPublicKey ecPublicKey;
    
    // PRivate key
    ECPrivateKey ecPrivateKey;
 	
    
 	
    public CAddress() 
    {
		
    }
    
    public CAddress(String pubKey) throws Exception
    {
    	// PRovider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		  
        // Key factory
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
		  
        // Key spec
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(pubKey));
		  
        // Public key
        this.ecPublicKey = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
		
	// Decode
	this.public_key=Base64.decodeBase64(public_key);
    }
        
	public CAddress(String public_key, 
                        String private_key)  throws Exception
	{
            // Key factory
            KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
		  
            // Encoded key
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(public_key));
		  
            // Public key
            this.ecPublicKey = (ECPublicKey) keyFactory.generatePublic(publicKeySpec);
		  
            // Encoded key spec
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(private_key));
		  
            // Private key
            this.ecPrivateKey = (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
		
	    // Public key
	    this.public_key=Base64.decodeBase64(public_key);
		
            // Private key
            this.private_key=Base64.decodeBase64(private_key);
	}
	
	public void generate() throws Exception
	{
            // Key generator
	    KeyPairGenerator keyPairGenerator =KeyPairGenerator.getInstance("ECDSA", "BC");
            
            // Load curve
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
            
            // Initialize
            keyPairGenerator.initialize(ecSpec, new SecureRandom());
            
            // Key pair
            KeyPair kp = keyPairGenerator.generateKeyPair();
            
            // Pub key
            ecPublicKey = (ECPublicKey) kp.getPublic();
		   
            // Private key
            ecPrivateKey = (ECPrivateKey) kp.getPrivate();
	
            // Encoded
            this.public_key=ecPublicKey.getEncoded();
        
            // Encoded private
            this.private_key=ecPrivateKey.getEncoded();
	}
	
	public String getPublic() 
	{
            return Base64.encodeBase64String(this.public_key);
        }
        
        
	public String getPrivate() 
	{
           return Base64.encodeBase64String(this.private_key);
        }
	
	public String encrypt(String data) throws Exception
	{
	    // Chiper
            Cipher c = javax.crypto.Cipher.getInstance("ECIESwithAES/DHAES/PKCS7Padding"); 
		   
            // Init
            c.init(c.ENCRYPT_MODE, this.ecPublicKey, new SecureRandom());   
		   
            // Encrypt
            byte[] encrypted=c.doFinal(data.getBytes(), 0, (data.getBytes()).length);
		   
            // Return
            return Base64.encodeBase64String(encrypted);
	}
	
	public String decrypt(String data) throws Exception
	{
            // Decode
	    byte[] d=Base64.decodeBase64(data);
	    
            // Choper
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("ECIESwithAES/DHAES/PKCS7Padding", "BC"); 
			
            // Init
            c.init(c.DECRYPT_MODE, this.ecPrivateKey, new SecureRandom());   
			
            // Decrypt
            byte[] decrypted=c.doFinal(d, 0, d.length);
			
            // Return
            return new String(decrypted);
	}
	
	public String sign(String data) throws Exception
	{
            // Signature
            Signature signature = Signature.getInstance("ECDSA", "BC");
	
            // Init signature
            signature.initSign(this.ecPrivateKey, new SecureRandom());
		   
            // Update
            signature.update(data.getBytes());
		   
            // Sign
            byte[] signed=signature.sign();
		   
            // Return
            return Base64.encodeBase64String(signed);
	}
        
        public boolean checkSig(String data, String sig) throws Exception
    {  
        // Signature
        Signature signature = Signature.getInstance("ECDSA", "BC");	
	
        // Verify
        signature.initVerify(this.ecPublicKey);
	
        // Update
        signature.update(data.getBytes());
		
	if (signature.verify(Base64.decodeBase64(sig)))
	    return true;
	else
	    return false;
    }
}