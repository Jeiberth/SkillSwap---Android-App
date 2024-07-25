package com.example.skillswap;

public class Thing {

        private String ThingId;
        private String NameThing;
        private String DescriptionThing;
        private String Owner;
        private boolean Available;
        private String ImageThing;

        // Default constructor required for calls to DataSnapshot.getValue(Thing.class)
        public Thing() {

        }

        // Constructor
        public Thing(String ThingId, String NameThing, String DescriptionThing, String Owner, boolean Available, String ImageThing) {
            this.ThingId = ThingId;
            this.NameThing = NameThing;
            this.DescriptionThing = DescriptionThing;
            this.Owner = Owner;
            this.Available = Available;
            this.ImageThing = ImageThing;
        }

        // Getters and setters
        public String getThingId() {
            return ThingId;
        }

        public void setThingId(String ThingId) {
            this.ThingId = ThingId;
        }

        public String getNameThing() {
            return NameThing;
        }

        public void setNameThing(String NameThing) {
            this.NameThing = NameThing;
        }

        public String getDescriptionThing() {
            return DescriptionThing;
        }

        public void setDescriptionThing(String DescriptionThing) {
            this.DescriptionThing = DescriptionThing;
        }

        public String getOwner() {
            return Owner;
        }

        public void setOwner(String Owner) {
            this.Owner = Owner;
        }

        public boolean isAvailable() {
            return Available;
        }

        public void setAvailable(boolean Available) {
            this.Available = Available;
        }

        public String getImageThing() {
            return ImageThing;
        }

        public void setImageThing(String ImageThing) {
            this.ImageThing = ImageThing;
        }



    /*
    private String DescriptionThing;
    private String ImageThing;
    private String NameThing;

    // Constructor vacío necesario para Firebase
    public Thing() {
    }

    // Constructor con parámetros
    public Thing(String NameThing, String DescriptionThing, String ImageThing) {
        this.NameThing = NameThing;
        this.DescriptionThing = DescriptionThing;
        this.ImageThing = ImageThing;
    }

    // Getters y setters
    public String getDescriptionThing() {
        return DescriptionThing;
    }

    public void setDescriptionThing(String DescriptionThing) {
        this.DescriptionThing = DescriptionThing;
    }

    public String getImageThing() {
        return ImageThing;
    }

    public void setImageThing(String ImageThing) {
        this.ImageThing = ImageThing;
    }

    public String getNameThing() {
        return NameThing;
    }

    public void setNameThing(String NameThing) {
        this.NameThing = NameThing;
    }


     */
}
