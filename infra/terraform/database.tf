# ── RDS Subnet Group ─────────────────────────────────────────

resource "aws_db_subnet_group" "main" {
  name       = "${var.app_name}-db-subnet-group"
  subnet_ids = aws_subnet.private[*].id
}

# ── RDS PostgreSQL Instance ───────────────────────────────────

resource "aws_db_instance" "postgres" {
  identifier        = "${var.app_name}-postgres"
  engine            = "postgres"
  engine_version    = "15.7"
  instance_class    = var.db_instance_class
  allocated_storage = 20
  storage_type      = "gp3"
  storage_encrypted = true

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  multi_az                = var.db_multi_az
  publicly_accessible     = false
  deletion_protection     = true
  skip_final_snapshot     = false
  final_snapshot_identifier = "${var.app_name}-final-snapshot"

  backup_retention_period = 7
  backup_window           = "03:00-04:00"
  maintenance_window      = "Mon:04:00-Mon:05:00"

  parameter_group_name = aws_db_parameter_group.postgres15.name

  tags = { Name = "${var.app_name}-postgres" }
}

resource "aws_db_parameter_group" "postgres15" {
  name   = "${var.app_name}-postgres15"
  family = "postgres15"

  parameter {
    name  = "log_min_duration_statement"
    value = "1000" # log queries > 1 second
  }
}

# ── DB credentials in Secrets Manager ────────────────────────

resource "aws_secretsmanager_secret" "db_credentials" {
  name                    = "${var.app_name}/db-credentials"
  recovery_window_in_days = 7
}

resource "aws_secretsmanager_secret_version" "db_credentials" {
  secret_id = aws_secretsmanager_secret.db_credentials.id
  secret_string = jsonencode({
    username = var.db_username
    password = var.db_password
    host     = aws_db_instance.postgres.address
    port     = 5432
    dbname   = var.db_name
    url      = "jdbc:postgresql://${aws_db_instance.postgres.address}:5432/${var.db_name}"
  })
}
