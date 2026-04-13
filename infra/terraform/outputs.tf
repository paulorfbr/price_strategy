output "frontend_bucket_name" {
  description = "S3 bucket name for frontend assets"
  value       = aws_s3_bucket.frontend.bucket
}

output "cloudfront_domain" {
  description = "CloudFront distribution domain — set this as your CNAME if using a custom domain"
  value       = aws_cloudfront_distribution.frontend.domain_name
}

output "cloudfront_distribution_id" {
  description = "CloudFront distribution ID — required for cache invalidation in CI/CD"
  value       = aws_cloudfront_distribution.frontend.id
}

output "ecr_repository_url" {
  description = "ECR repository URL for the backend Docker image"
  value       = aws_ecr_repository.backend.repository_url
}

output "alb_dns_name" {
  description = "ALB DNS name for the Spring Boot backend API"
  value       = aws_lb.backend.dns_name
}

output "rds_endpoint" {
  description = "RDS PostgreSQL endpoint (private — accessible only from VPC)"
  value       = aws_db_instance.postgres.address
  sensitive   = true
}

output "db_secret_arn" {
  description = "ARN of the Secrets Manager secret holding DB credentials"
  value       = aws_secretsmanager_secret.db_credentials.arn
}
