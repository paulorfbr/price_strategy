variable "aws_region" {
  description = "AWS region for all resources (except CloudFront ACM which is always us-east-1)"
  type        = string
  default     = "eu-west-1"
}

variable "environment" {
  description = "Deployment environment name"
  type        = string
  default     = "prod"
}

variable "app_name" {
  description = "Application identifier used as a prefix for resource names"
  type        = string
  default     = "prix-strategie"
}

# ── Frontend ─────────────────────────────────────────────────

variable "domain_name" {
  description = "Custom domain for CloudFront (e.g. app.example.com). Leave empty to use the CloudFront *.cloudfront.net domain."
  type        = string
  default     = ""
}

# ── Backend ──────────────────────────────────────────────────

variable "backend_image_tag" {
  description = "Docker image tag to deploy to ECS (e.g. git SHA)"
  type        = string
  default     = "latest"
}

variable "backend_cpu" {
  description = "ECS task CPU units (256 = 0.25 vCPU)"
  type        = number
  default     = 512
}

variable "backend_memory" {
  description = "ECS task memory in MiB"
  type        = number
  default     = 1024
}

variable "backend_desired_count" {
  description = "Number of ECS tasks to run"
  type        = number
  default     = 1
}

# ── Database ─────────────────────────────────────────────────

variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.micro"
}

variable "db_name" {
  description = "PostgreSQL database name"
  type        = string
  default     = "prixstrategie"
}

variable "db_username" {
  description = "PostgreSQL master username"
  type        = string
  default     = "prixadmin"
  sensitive   = true
}

variable "db_password" {
  description = "PostgreSQL master password — provide via TF_VAR_db_password or AWS Secrets Manager"
  type        = string
  sensitive   = true
}

variable "db_multi_az" {
  description = "Enable RDS Multi-AZ for high availability"
  type        = bool
  default     = false
}

# ── Networking ───────────────────────────────────────────────

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "List of AZs to use (at least 2 required for RDS)"
  type        = list(string)
  default     = ["eu-west-1a", "eu-west-1b"]
}
