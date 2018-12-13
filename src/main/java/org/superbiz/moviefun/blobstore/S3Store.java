package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {
    private final AmazonS3Client amazonS3Client;
    private final String storageBucket;


    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.amazonS3Client = s3Client;
        this.storageBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(blob.contentType);
        amazonS3Client.putObject(new PutObjectRequest(storageBucket, blob.name, blob.inputStream, objectMetadata));
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        if (amazonS3Client.doesObjectExist(storageBucket, name)) {
            S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(storageBucket, name));

            return Optional.of(new Blob(name,
                            s3Object.getObjectContent(),
                            s3Object.getObjectMetadata().getContentType()
                    )
            );
        }
        else
            return Optional.empty();
    }

    @Override
    public void deleteAll() {

    }
}
