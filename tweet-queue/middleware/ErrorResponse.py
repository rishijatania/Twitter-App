class ErrorResponseMessage:
	def __init__(self, message, timestamp,status,error,path):
		self.message = message
		self.timestamp = timestamp
		self.status = status
		self.error = error
		self.path = path