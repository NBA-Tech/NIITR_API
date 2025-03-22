package com.niitr_api.niitr_api.Services;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;


@Service
public class CloudNiaryService {
    private Cloudinary cloudinary;
    private Dotenv dotenv=Dotenv.load();
    private final String CLOUDINARY_CLOUD_NAME = dotenv.get("CLOUDINARY_CLOUD_NAME");
    private final String CLOUDINARY_API_KEY = dotenv.get("CLOUDINARY_API_KEY");
    private final String CLOUDINARY_API_SECRET = dotenv.get("CLOUDINARY_API_SECRET");

    public CloudNiaryService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name",this.CLOUDINARY_CLOUD_NAME,
            "api_key",this.CLOUDINARY_API_KEY,
            "api_secret",this.CLOUDINARY_API_SECRET

        ));
    }

    public List<String> putCloudinaryImage(List<String> cloudinaryImage){
        List<String> image_url=new ArrayList<String>();
        
        for(String image:cloudinaryImage){
            try{
                Map uploadResult = this.cloudinary.uploader().upload(
                image,
                ObjectUtils.asMap("resource_type", "image")
            );
            image_url.add((String)uploadResult.get("secure_url"));
            }
            catch(IOException e){
                e.printStackTrace();
                return image_url;
            }
        }
        return image_url;
    }
    
}
