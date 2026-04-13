terraform {
  required_version = ">= 1.7"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.50"
    }
  }

  # Remote state — create this bucket/table manually before first apply,
  # or bootstrap with: terraform init -backend=false && terraform apply -target=aws_s3_bucket.tf_state
  backend "s3" {
    bucket         = "prix-strategie-tfstate"
    key            = "prod/terraform.tfstate"
    region         = "us-west-1"
    encrypt        = true
    use_lockfile   = true
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "prix-strategie"
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}

# Second provider for us-east-1 — required for CloudFront ACM certificates
provider "aws" {
  alias  = "us_east_1"
  region = "us-east-1"

  default_tags {
    tags = {
      Project     = "prix-strategie"
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}
