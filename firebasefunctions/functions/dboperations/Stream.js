const config = require('../config');
const firebase_fields = config.firebase_fields ;

exports.createStream = function(dbRef,streamTitle,user,callback){
	let newStreamId = dbRef.ref(firebase_fields.STREAMS).push();
	const created_at = ""+(new Date()).toISOString();
	const updated_at = ""+(new Date()).toISOString();
	let stream = {
		"name" : streamTitle,
		"created_at": created_at,
		"updated_at": updated_at,
		"owner": user
	}
	newStreamId.set(stream,callback); 
	console.log("createStream "+JSON.stringify(stream));
	return newStreamId.key;
}

exports.getAllStream = function(dbRef,regionId){
	
}