package com.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Scanner;

import javax.persistence.EntityManager;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.jpa.test.PersistenceManager;
import com.jpa.test.Test;
import com.mysql.db.test.MySQLJava;

public class DataProcessor implements RequestHandler<S3Event, String>{

	public String handleRequest(S3Event s3Event, Context context) {
		
		try{
			LambdaLogger logger = context.getLogger();
			S3EventNotificationRecord record = s3Event.getRecords().get(0);

            String bucketName = record.getS3().getBucket().getName();
            
            logger.log("bucket name: "+bucketName);
            String inputKey = record.getS3().getObject().getKey()
                    .replace('+', ' ');
            logger.log("input key "+inputKey );
            inputKey = URLDecoder.decode(inputKey, "UTF-8");
            
            AmazonS3 s3Client = new AmazonS3Client();
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(
                    bucketName, inputKey));
            
            InputStream is = s3Object.getObjectContent();
            // process the file and put the date in DB
            
            //testing the db connection
            System.out.println("testing the db connection-----");
            //testDBConnection();
            testDBConnectionJPA();
            
            logger.log("processed the file with latest java!");
            // copy the file to different folder
            String outputKey = "output/processed-"+inputKey;
            try {
                CopyObjectRequest copyObjRequest = new CopyObjectRequest(
                		bucketName, inputKey, bucketName, outputKey);
                s3Client.copyObject(copyObjRequest);
                logger.log("file got copied-----");
            } catch (AmazonServiceException ase) {
                logger.log("Caught an AmazonServiceException, " +
                		"which means your request made it " + 
                		"to Amazon S3, but was rejected with an error " +
                        "response for some reason.");
                logger.log("Error Message:    " + ase.getMessage());
                logger.log("HTTP Status Code: " + ase.getStatusCode());
                logger.log("AWS Error Code:   " + ase.getErrorCode());
                logger.log("Error Type:       " + ase.getErrorType());
                logger.log("Request ID:       " + ase.getRequestId());
            } catch (AmazonClientException ace) {
                logger.log("Caught an AmazonClientException, " +
                		"which means the client encountered " +
                        "an internal error while trying to " +
                        " communicate with S3, " +
                        "such as not being able to access the network.");
                logger.log("Error Message: " + ace.getMessage());
            }
            
            // delete the original file
            s3Client.deleteObject(bucketName, inputKey);
           
			return "file processed";
		}catch(IOException ioe){
			throw new RuntimeException(ioe);
		}
		
	}

	private void testDBConnectionJPA() {
			System.out.println("testing jpa db connection================");
		 	Test test = new Test();
		    test.setId(new Integer(39));
		    test.setText("testing jpa");
		 
		    EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
		    System.out.println("entity manager created:::: "+em);
		    
		    readExistingValue(em);
		    
		    em.getTransaction()
		        .begin();
		    System.out.println("beginning the trasaction:::: ");
		    em.persist(test);
		    em.getTransaction()
		        .commit();
		    System.out.println("commit done-------");
		    
		    em.close();
		    PersistenceManager.INSTANCE.close();
	}

	private void readExistingValue(EntityManager em) {
		System.out.println("reading existing value::::::: "+em);
		Test testObject = em.find(Test.class, 100);
		if(testObject != null){
			System.out.println("id=====: "+testObject.getId());
			System.out.println("text======: "+testObject.getText());
		}
	}

	private void testDBConnection() {
		
		String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
		String MYSQL_URL = "jdbc:mysql://<DB url>/<dbname>?"
	            + "user=<username>&password=<password>";
		
		MySQLJava dao = new MySQLJava(MYSQL_DRIVER,MYSQL_URL);
        try {
			dao.readData();
		} catch (Exception e) {
			System.out.println("error occurred-----"+e);
			e.printStackTrace();
		}
	}

}
