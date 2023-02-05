# CachingDatabase

Usage : 

  Add it in your root build.gradle at the end of repositories:
``` groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```  
  Add the dependency in app gradle :
``` groovy  
	dependencies {
		implementation 'com.github.AnonymousAliensX:CachingDatabase:1.02'
	}
```  
  
  Enjoy :)

  How to use :
	
	For listening value changes :-
``` java
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
```
For putting or updating value at specific location :-
``` java
CachingDatabase.getInstance().getReference().child("Student").child("Name")
			.putValue("AnonymousAlien");
```

If you want to get callback of putValue success, you can use :-

``` java
CachingDatabase.getInstance().getReference().child("Student").child("Name")
	.putValue("AnonymousAlien", new CachingReferenceCallbacks() {
		@Override
		public void onSuccess(String message) {
			super.onSuccess(message);
		}
	});
```
