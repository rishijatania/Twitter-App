package com.twitter.twitterbackend.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import javax.annotation.PostConstruct;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.twitter.twitterbackend.models.User;
import com.twitter.twitterbackend.security.services.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    @Value("${amazonProperties.bucketName}")
	private String bucketName;

	private AmazonS3 s3client;

	@Autowired
	UserDetailsServiceImpl userServiceImpl;

	@PostConstruct
	private void initializeAmazon() {
		this.s3client = AmazonS3ClientBuilder
			.standard()
			.withRegion(Regions.US_EAST_1)
			.build();
	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		try {
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return convFile;
	}

	public String uploadFile(MultipartFile multipartFile) throws Exception {
		String fileName="";
		try {
			File file = convertMultiPartToFile(multipartFile);
			fileName = generateFileName(multipartFile);
			uploadFileTos3bucket(fileName, file);
			file.delete();
		} catch (Exception e) {
		   e.printStackTrace();
		   throw e;
		}
		return fileName;
	}

	private void uploadFileTos3bucket(String fileName, File file) throws Exception{
		try {
			s3client.putObject(new PutObjectRequest(bucketName, fileName, file));
				// .withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			throw e;
		}
	}

	private String generateFileName(MultipartFile multiPart) {
		User user = userServiceImpl.getCurrentUserFromSession();
		return new Date().getTime() + "-" + user.getUsername() + "-" + "profilePic"; 
	}

	public String[] updateImage(MultipartFile file,String fileName) throws Exception {
		String name="";
		String url="";
		if(file == null && fileName==null)
			return null;
		if(fileName!=null){
			deleteFileFromS3Bucket(fileName);
		}
		if(file != null){
			name = uploadFile(file);
			url = generatePresignedURL(name);
		}
		return new String[]{name,url};
	}

	private void deleteFileFromS3Bucket(String fileName) throws IOException {
		try{
			s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			throw e;
		}
	}	

	public String generatePresignedURL(String objectKey)
    {
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, objectKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }
}
