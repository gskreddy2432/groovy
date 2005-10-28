class BlogController {
	
	@Property String defaultAction = "list"
	
	@Property init = false
	
	@Property Closure list = {		
		createTestData()
		
		def blogOwner = Owner.findByLogin( this.params["name"] )

		return [ "blog": blogOwner.blog ];
		
	}
	
	def createTestData() {
		def owner = new Owner()
		owner.firstName = "Joe"
		owner.lastName = "Blogs"
		owner.login = "jblogs"
		owner.password = "me"
		owner.email = "joe.blogs@blogs.com"
		
		def blog = new Blog()
		blog.owner = owner
		blog.name = "Joe's Blog"
		owner.blog = blog
		
		def entry = new BlogEntry()
		entry.title = "Test Entry"
		entry.date = new Date()
		entry.body = "This is a test entry in this demo blog"
		blog.entries.add(entry)
		
		
		def comment = new Comment()
		comment.entry = entry
		comment.authorName = "Fred Flintstone"
		comment.authorEmail = "fred@blogs.com"
		comment.authorBlogURL = "http://www.blogs.com/fred"
		comment.body = "This is my comment!"		
		entry.comments.add(comment)
		
		owner.save()
		
	}
}
