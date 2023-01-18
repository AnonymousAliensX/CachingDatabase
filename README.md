# CachingDatabase

Usage : 

  Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  Add the dependency in app gradle :
  
	dependencies {
	        implementation 'com.github.AnonymousAliensX:CachingDatabase:1.01'
	}
  
  
  Enjoy :)

  How to use :
	
	For listening value changes :-

		CachingDatabase.getInstance().getReference().child("Student").child("Name")
					.listen(new ValueListener() {
						@Override
						public void onSuccess(DataSnapShot dataSnapShot) {
							String name = dataSnapShot.getValue(String.class, "default_value");
						}

						@Override
						public void onFailure(Exception e) {
							e.printStackTrace();
						}
					});

	For putting or updating value at specific location :-

		CachingDatabase.getInstance().getReference().child("Student").child("Name")
					.putValue("AnonymousAlien");
	
		If you want to get callback of putValue success, you can use :-

			CachingDatabase.getInstance().getReference().child("Student").child("Name")
                .putValue("AnonymousAlien", new CachingReferenceCallbacks() {
                    @Override
                    public void onSuccess(String message) {
                        super.onSuccess(message);
                    }
                });
