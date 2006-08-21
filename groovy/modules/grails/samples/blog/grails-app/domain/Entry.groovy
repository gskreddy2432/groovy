class Entry { 
	@Property Long id; 
	@Property Long version; 
	@Property relationships = [ "comments" : Comment.class ]
	
	@Property String title
	@Property Date date = new Date()
	@Property String body
	@Property Set comments = new HashSet()
	
	@Property constraints = {
		title(blank:false,length:1..50)
		date(nullable:false)
		body(blank:false)
	}
}	