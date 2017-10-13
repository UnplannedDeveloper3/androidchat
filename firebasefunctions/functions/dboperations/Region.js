const config = require('../config');
const firebase_fields = config.firebase_fields ;

exports.createRegion = function(dbRef,region,callback){
	let regionIdRef = dbRef.ref(firebase_fields.REGIONS).push();
	console.log("regionIdRef : "+regionIdRef+" firebase_fields.REGIONS "+firebase_fields.REGIONS);
	regionIdRef.set(region,callback) ;
	return regionIdRef.key;
}

exports.addStreamIntoRegion = function(dbRef,regionId,streamId,callback){
	//const created_at = (new Date());
	const updated_at = ""+(new Date()).toISOString(); 
	
	console.log("addStreamIntoRegion : regionId: "+regionId+" streamId : "+streamId);
	console.log("dbPath > "+firebase_fields.REGIONS+"/"+regionId);
	dbRef.ref(firebase_fields.REGIONS+"/"+regionId)
		.child('streams').child(streamId).set(true);
	dbRef.ref(firebase_fields.REGIONS+"/"+regionId)
		.child('updated_at').set(updated_at,callback);
} 

exports.getAllRegion =function(dbRef,callback){
	//getAllRegion
}