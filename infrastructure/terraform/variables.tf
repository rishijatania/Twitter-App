#
# Variables Configuration
#
variable "cluster-name" {
  default = "terraform-eks-twitter-app"
  type    = string
}

variable "region" {
  description = "Aws region"
  default     = "us-east-1"
}
variable "profile" {
  description = "Aws cli profile"
  default     = "default"
}