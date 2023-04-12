package com.example.businesscarapp;

public class ArticleModel
{
    public String writerName;
    public String title;
    public String createdAt;
    public String imageUrl;
    public String contents;
    public String ProfileImage;

    public ArticleModel()
    {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ArticleModel(String writerName, String title, String createdAt, String imageUrl, String contents, String ProfileImage)
    {
        this.writerName = writerName;
        this.createdAt = createdAt;
        this.title = title;
        this.imageUrl = imageUrl;
        this.contents = contents;
        this.ProfileImage = ProfileImage;
    }

    public String getWriterName()
    {
        return writerName;
    }

    public void setWriterName(String writerName)
    {
        this.writerName = writerName;
    }


    public String getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(String createdAt)
    {
        this.createdAt = createdAt;
    }


    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getContents()
    {
        return contents;
    }

    public void setContents(String contents)
    {
        this.contents = contents;
    }


    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getprofileImageUrl()
    {
        return ProfileImage;
    }

    public void setProfileImageUrl(String ProfileImage)
    {
        this.ProfileImage = ProfileImage;
    }
}
