const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const config = require('./config');
const firebase_fields = config.firebase_fields;
const regions = require('./dboperations/Region');
const stream = require('./dboperations/Stream');
/*
 *	Logic :
 *	create New Stream
 *	First create region if not present in db
 *	Add streamId into region
 *
 *	Implementation
 *	Regions are created on basis of Location
 */

exports.addStreamIntoRegion = functions.https.onRequest((req, res) => {
		//	req.location.latitude
		//	req.location.longitude
		//	req.stream.title
		//	req.region.locality -> Mumbai
		//	req.region.sublocality -> Ghatakopar west
		let regionName = "";
		let streamTitle = "";
		let currentUserLatitude = "";
		let currentUserLongitude = "";
		let reqBody = req.body;
		let user = reqBody.user

		console.log("Add Region: " + JSON.stringify(reqBody));

		//regions.createRegion(reqBody);
		
		function successResponse(){
				//setTimeout(function(){
					res.status(200).json({"message" : "Success"}); 
				//},1000);
		}

		if (reqBody.region && reqBody.location) {
			regionName = reqBody.region.sublocality.trim() + ", " + reqBody.region.locality.trim();

			if (reqBody.stream.name) {
				streamTitle = reqBody.stream.name;
			}
			currentUserLatitude = reqBody.location.latitude;
			currentUserLongitude = reqBody.location.longitude; 

			let regionDbReference = admin.database().ref(firebase_fields.REGIONS);
			console.log("Owner : "+JSON.stringify(user)); 
			let streamId = stream.createStream(admin.database(), streamTitle,user,function(){});

			regionDbReference.orderByChild(firebase_fields.NAME) 
			.equalTo(regionName)
			.once('value')
			.then(region => {
				console.log("The " + region.key + " score is " + JSON.stringify(region.val()));
				//console.log("\n***** childKey"+region.keys()[0]);
				let regionId = '';
				if (region.val()) {
					// Add stream in existing region
					region.forEach(function (childSnapshot) {
						console.log("In Foreach" + childSnapshot.key); 
						regionId = childSnapshot.key;
					});
					regions.addStreamIntoRegion(admin.database(), regionId, streamId,successResponse);
				} else {
					const created_at = ""+(new Date()).toISOString();
					
					// Create New Region`
					let newRegion = {
						"name": regionName,
						"latitude": currentUserLatitude,
						"longitude": currentUserLongitude,
						"created_at": created_at,
						"updated_at": created_at,
						"streams": {}
					};
					console.log("New Region created > "+JSON.stringify(newRegion));
					
					newRegion.streams[streamId] = true;
					regions.createRegion(admin.database(), newRegion,successResponse);
				}
				
				//res.status(200).send("Success");
			});
		} else {
			res.status(422).json({"message":"Fail"});
			//res.status(422).send("Fail");
		}
	});

exports.getAllRegion = functions.https.onRequest((req, res) => {});


// When New stream is created all the user are notified
/*exports.notifyUserOnStreamAdd = functions.database.ref('/streams/{streamId}')
	.onWrite(event => {
		const original = event.data.val();
		console.log('', event.params.streamId, original);
		
	});

// When new message add in 
exports.notifyUserOnNewMessageAdded = functions.database.ref('/messages/{streamId}/{messageId}')
	.onWrite(event => {
		const streamId = event.params.streamId ;
		const messageId = event.params.messageId;
		console.log(" * "+streamId+"  "+messageId);
		//const original = event.data.val();
		//console.log('', event.params.streamId+" : "+messageId, original);
		
	});*/