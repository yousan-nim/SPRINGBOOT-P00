"use client";

import { FormEvent, useEffect, useMemo, useState } from "react";
import styles from "./page.module.css";

type CategoryType = "INCOME" | "EXPENSE";

type Category = {
  id: number;
  name: string;
  type: CategoryType;
  color: string;
};

type Transaction = {
  id: number;
  title: string;
  amount: number;
  type: CategoryType;
  transactionDate: string;
  note: string | null;
  category: Category;
};

type Summary = {
  month: string;
  totalIncome: number;
  totalExpense: number;
  balance: number;
  transactionCount: number;
  expenseByCategory: Record<string, number>;
};

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080/v1/api";

const currentMonth = new Date().toISOString().slice(0, 7);

export default function Home() {
  const [month, setMonth] = useState(currentMonth);
  const [categories, setCategories] = useState<Category[]>([]);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [summary, setSummary] = useState<Summary | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState({
    title: "",
    amount: "",
    type: "EXPENSE" as CategoryType,
    transactionDate: new Date().toISOString().slice(0, 10),
    categoryId: "",
    note: "",
  });

  const filteredCategories = useMemo(
    () => categories.filter((category) => category.type === form.type),
    [categories, form.type],
  );

  useEffect(() => {
    void initialize();
  }, []);

  useEffect(() => {
    if (!filteredCategories.find((category) => String(category.id) === form.categoryId)) {
      setForm((prev) => ({
        ...prev,
        categoryId: filteredCategories[0] ? String(filteredCategories[0].id) : "",
      }));
    }
  }, [filteredCategories, form.categoryId]);

  async function initialize() {
    try {
      setError(null);
      await loadCategories();
      await loadDashboard(currentMonth);
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : "Unknown error");
      setLoading(false);
    }
  }

  async function loadCategories() {
    const response = await fetch(`${API_BASE_URL}/categories`);
    if (!response.ok) {
      throw new Error("Failed to load categories");
    }
    const data = (await response.json()) as Category[];
    setCategories(data);
  }

  async function loadDashboard(targetMonth: string) {
    try {
      setLoading(true);
      setError(null);

      const [transactionsResponse, summaryResponse] = await Promise.all([
        fetch(`${API_BASE_URL}/transactions?month=${targetMonth}`),
        fetch(`${API_BASE_URL}/reports/summary?month=${targetMonth}`),
      ]);

      if (!transactionsResponse.ok || !summaryResponse.ok) {
        throw new Error("Failed to load dashboard data");
      }

      setTransactions((await transactionsResponse.json()) as Transaction[]);
      setSummary((await summaryResponse.json()) as Summary);
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : "Unknown error");
    } finally {
      setLoading(false);
    }
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      const response = await fetch(`${API_BASE_URL}/transactions`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          title: form.title,
          amount: Number(form.amount),
          type: form.type,
          transactionDate: form.transactionDate,
          categoryId: Number(form.categoryId),
          note: form.note || null,
        }),
      });

      if (!response.ok) {
        const payload = (await response.json()) as { message?: string };
        throw new Error(payload.message ?? "Failed to create transaction");
      }

      setForm((prev) => ({
        ...prev,
        title: "",
        amount: "",
        note: "",
      }));
      await loadDashboard(month);
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : "Unknown error");
    } finally {
      setSubmitting(false);
    }
  }

  async function handleDelete(id: number) {
    try {
      const response = await fetch(`${API_BASE_URL}/transactions/${id}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        throw new Error("Failed to delete transaction");
      }

      await loadDashboard(month);
    } catch (deleteError) {
      setError(deleteError instanceof Error ? deleteError.message : "Unknown error");
    }
  }

  return (
    <div className={styles.page}>
      <main className={styles.main}>
        <section className={styles.hero}>
          <div>
            <p className={styles.eyebrow}>Expense Tracker Portfolio</p>
            <h1>Full-stack starter สำหรับโชว์งาน Spring Boot + Next.js</h1>
            <p className={styles.description}>
              เพิ่มรายการรายรับรายจ่าย, ดู monthly summary, และต่อ PostgreSQL ผ่าน Docker Compose ได้ทันที
            </p>
          </div>
          <label className={styles.monthPicker}>
            <span>Month</span>
            <input
              type="month"
              value={month}
              onChange={(event) => {
                const nextMonth = event.target.value;
                setMonth(nextMonth);
                void loadDashboard(nextMonth);
              }}
            />
          </label>
        </section>

        {error ? <p className={styles.error}>{error}</p> : null}

        <section className={styles.summaryGrid}>
          <article className={styles.card}>
            <span>Income</span>
            <strong className={styles.income}>
              {summary ? currency(summary.totalIncome) : "..."}
            </strong>
          </article>
          <article className={styles.card}>
            <span>Expense</span>
            <strong className={styles.expense}>
              {summary ? currency(summary.totalExpense) : "..."}
            </strong>
          </article>
          <article className={styles.card}>
            <span>Balance</span>
            <strong>{summary ? currency(summary.balance) : "..."}</strong>
          </article>
          <article className={styles.card}>
            <span>Transactions</span>
            <strong>{summary ? summary.transactionCount : "..."}</strong>
          </article>
        </section>

        <section className={styles.contentGrid}>
          <article className={`${styles.panel} ${styles.formPanel}`}>
            <div className={styles.panelHeader}>
              <h2>Add transaction</h2>
              <p>ใช้ฟอร์มนี้เติมข้อมูลจริงลง PostgreSQL</p>
            </div>
            <form className={styles.form} onSubmit={handleSubmit}>
              <label>
                <span>Title</span>
                <input
                  required
                  value={form.title}
                  onChange={(event) =>
                    setForm((prev) => ({ ...prev, title: event.target.value }))
                  }
                />
              </label>
              <div className={styles.inlineFields}>
                <label>
                  <span>Amount</span>
                  <input
                    required
                    min="0.01"
                    step="0.01"
                    type="number"
                    value={form.amount}
                    onChange={(event) =>
                      setForm((prev) => ({ ...prev, amount: event.target.value }))
                    }
                  />
                </label>
                <label>
                  <span>Type</span>
                  <select
                    value={form.type}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        type: event.target.value as CategoryType,
                      }))
                    }
                  >
                    <option value="EXPENSE">Expense</option>
                    <option value="INCOME">Income</option>
                  </select>
                </label>
              </div>
              <div className={styles.inlineFields}>
                <label>
                  <span>Date</span>
                  <input
                    required
                    type="date"
                    value={form.transactionDate}
                    onChange={(event) =>
                      setForm((prev) => ({
                        ...prev,
                        transactionDate: event.target.value,
                      }))
                    }
                  />
                </label>
                <label>
                  <span>Category</span>
                  <select
                    required
                    value={form.categoryId}
                    onChange={(event) =>
                      setForm((prev) => ({ ...prev, categoryId: event.target.value }))
                    }
                  >
                    {filteredCategories.map((category) => (
                      <option key={category.id} value={category.id}>
                        {category.name}
                      </option>
                    ))}
                  </select>
                </label>
              </div>
              <label>
                <span>Note</span>
                <textarea
                  rows={3}
                  value={form.note}
                  onChange={(event) =>
                    setForm((prev) => ({ ...prev, note: event.target.value }))
                  }
                />
              </label>
              <button disabled={submitting || !form.categoryId} type="submit">
                {submitting ? "Saving..." : "Save transaction"}
              </button>
            </form>
          </article>

          <article className={styles.panel}>
            <div className={styles.panelHeader}>
              <h2>Recent activity</h2>
              <p>{loading ? "Loading data..." : `${transactions.length} records`}</p>
            </div>
            <div className={styles.transactionList}>
              {transactions.map((transaction) => (
                <div className={styles.transactionItem} key={transaction.id}>
                  <div>
                    <p>{transaction.title}</p>
                    <small>
                      {transaction.category.name} · {transaction.transactionDate}
                    </small>
                  </div>
                  <div className={styles.transactionMeta}>
                    <strong
                      className={
                        transaction.type === "INCOME" ? styles.income : styles.expense
                      }
                    >
                      {transaction.type === "INCOME" ? "+" : "-"}
                      {currency(transaction.amount)}
                    </strong>
                    <button
                      className={styles.deleteButton}
                      onClick={() => void handleDelete(transaction.id)}
                      type="button"
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))}
              {!loading && transactions.length === 0 ? (
                <p className={styles.emptyState}>ยังไม่มีรายการในเดือนนี้</p>
              ) : null}
            </div>
          </article>
        </section>

        <section className={styles.panel}>
          <div className={styles.panelHeader}>
            <h2>Expense breakdown</h2>
            <p>รวมยอดรายจ่ายแยกตามหมวดหมู่</p>
          </div>
          <div className={styles.breakdownList}>
            {summary && Object.entries(summary.expenseByCategory).length > 0 ? (
              Object.entries(summary.expenseByCategory).map(([name, amount]) => (
                <div className={styles.breakdownItem} key={name}>
                  <span>{name}</span>
                  <strong>{currency(amount)}</strong>
                </div>
              ))
            ) : (
              <p className={styles.emptyState}>ยังไม่มีข้อมูลรายจ่ายสำหรับเดือนนี้</p>
            )}
          </div>
        </section>
      </main>
    </div>
  );
}

function currency(value: number) {
  return new Intl.NumberFormat("th-TH", {
    style: "currency",
    currency: "THB",
    maximumFractionDigits: 2,
  }).format(value);
}
